package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.AttributeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.MultiplyAttributeConfig;
import name.modid.core.api.modifiers.helpers.GemstoneAttributeModifiersHelper;
import name.modid.core.api.modifiers.helpers.ModifierHelper;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class AttributeModifierHandler {
  private static final String VANILLA_NAMESPACE = "minecraft";
  // 1.8 blocks * (1 - 0.72) = 0.5 blocks
  private static final float MIN_SCALE_TOTAL = -0.72F;

  private record AttributeGroupKey(RegistryEntry<EntityAttribute> attribute, Operation operation) {
  }

  public static void apply(ArrayList<GemstoneModifier> gemstoneModifiers, ItemStack itemStack) {
    AttributeModifiersComponent expected = buildComponent(gemstoneModifiers, itemStack);
    if (expected == null) {
      return;
    }

    itemStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, expected);
  }

  // True when the baked attribute modifiers of the stack differ from the current gemstone configs
  public static boolean isStale(ArrayList<GemstoneModifier> gemstoneModifiers, ItemStack itemStack) {
    AttributeModifiersComponent expected = buildComponent(gemstoneModifiers, itemStack);
    if (expected == null) {
      return false;
    }

    AttributeModifiersComponent current = itemStack.getOrDefault(
        DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT);
    return !expected.equals(current);
  }

  // Returns null when the stack must be left untouched
  private static AttributeModifiersComponent buildComponent(
      ArrayList<GemstoneModifier> gemstoneModifiers,
      ItemStack itemStack) {
    List<AttributeModifiersComponent.Entry> gemstoneEntries = buildGemstoneEntries(gemstoneModifiers,
        itemStack.getItem());

    // If there are no gemstone attribute modifiers to apply, we must not write the
    // ATTRIBUTE_MODIFIERS component onto the stack: doing so would shadow
    // stack-aware dynamic attribute modifiers computed by other mods, even when no
    // gemstones are present. Only clean up gemstone modifiers this mod previously wrote.
    if (gemstoneEntries.isEmpty() && !hasGemstoneAttributeModifiers(itemStack)) {
      return null;
    }

    Map<String, AttributeModifiersComponent.Entry> combinedModifiersMap = new LinkedHashMap<>();
    Function<AttributeModifiersComponent.Entry, String> entryKey = e -> String.format("%s.%s.%s", e.modifier().id(),
        e.slot(), e.attribute().value());

    for (AttributeModifiersComponent.Entry entry : getBaseEntries(itemStack)) {
      combinedModifiersMap.put(entryKey.apply(entry), entry);
    }

    for (AttributeModifiersComponent.Entry entry : gemstoneEntries) {
      combinedModifiersMap.put(entryKey.apply(entry), entry);
    }

    return new AttributeModifiersComponent(new ArrayList<>(combinedModifiersMap.values()), true);
  }

  // Base stats always come from the item itself, so an upgraded item (diamond -> netherite)
  // does not keep the copied stats of the previous item. Dynamic modifiers of other mods
  // (non-vanilla namespaces) are preserved.
  private static List<AttributeModifiersComponent.Entry> getBaseEntries(ItemStack itemStack) {
    AttributeModifiersComponent prototype = itemStack.getItem().getComponents()
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
    List<AttributeModifiersComponent.Entry> entries = new ArrayList<>(prototype.modifiers());

    AttributeModifiersComponent existing = GemstoneAttributeModifiersHelper.getEffectiveModifiers(itemStack);
    for (AttributeModifiersComponent.Entry entry : existing.modifiers()) {
      Identifier id = entry.modifier().id();
      if (id == null
          || id.getNamespace().equalsIgnoreCase(Gemstones.MOD_ID)
          || id.getNamespace().equals(VANILLA_NAMESPACE)) {
        continue;
      }

      entries.add(entry);
    }

    return entries;
  }

  private static List<AttributeModifiersComponent.Entry> buildGemstoneEntries(
      ArrayList<GemstoneModifier> gemstoneModifiers,
      Item item) {
    // Values are summed only within the same attribute and operation, otherwise a TOTAL penalty
    // could be merged into a BASE bonus (and vice versa)
    Map<AttributeGroupKey, List<GemstoneModifier>> groupedModifiers = new LinkedHashMap<>();
    Map<RegistryEntry<EntityAttribute>, Integer> operationsPerAttribute = new LinkedHashMap<>();

    for (GemstoneModifier modifier : gemstoneModifiers) {
      for (AttributeConfig config : getAttributeConfigs(modifier)) {
        AttributeGroupKey key = new AttributeGroupKey(config.attribute(), config.operation());
        if (!groupedModifiers.containsKey(key)) {
          operationsPerAttribute.merge(config.attribute(), 1, Integer::sum);
        }
        groupedModifiers.computeIfAbsent(key, k -> new ArrayList<>()).add(modifier);
      }
    }

    List<AttributeModifiersComponent.Entry> gemstoneEntries = new ArrayList<>();
    for (Map.Entry<AttributeGroupKey, List<GemstoneModifier>> group : groupedModifiers.entrySet()) {
      AttributeGroupKey key = group.getKey();
      float totalValue = 0f;
      GemstoneModifier firstModifier = null;

      for (GemstoneModifier modifier : group.getValue()) {
        GemstoneQuality rarity = modifier.getRarityType();
        for (AttributeConfig config : getAttributeConfigs(modifier)) {
          if (config.attribute().equals(key.attribute()) && config.operation() == key.operation()) {
            totalValue += config.values().get(rarity);
            if (firstModifier == null) {
              firstModifier = modifier;
            }
          }
        }
      }

      if (firstModifier == null) {
        continue;
      }

      // Jade: the player can shrink to half a block (still able to walk through 1 block high gaps), not further
      if (key.attribute().equals(EntityAttributes.GENERIC_SCALE) && key.operation() == Operation.ADD_MULTIPLIED_TOTAL) {
        totalValue = Math.max(totalValue, MIN_SCALE_TOTAL);
      }

      EquipmentSlot slot = ModifierHelper.getEquipmentSlot(item);
      String path = String.format("%s.%s.%s",
          firstModifier.getGemstoneType().toString().toLowerCase(),
          firstModifier.getItemCategory().toString().toLowerCase(),
          slot.name().toLowerCase());

      // Keep the old id when the attribute has a single operation, so existing items stay stable
      if (operationsPerAttribute.getOrDefault(key.attribute(), 1) > 1) {
        path = path + "." + key.operation().asString();
      }

      EntityAttributeModifier scaledGemstoneModifier = new EntityAttributeModifier(
          Identifier.of(Gemstones.MOD_ID, path),
          (double) totalValue,
          key.operation());

      gemstoneEntries.add(new AttributeModifiersComponent.Entry(
          key.attribute(),
          scaledGemstoneModifier,
          ModifierHelper.getAttributeModifierSlot(item)));
    }

    return gemstoneEntries;
  }

  private static List<AttributeConfig> getAttributeConfigs(GemstoneModifier modifier) {
    if (modifier.getConfig() instanceof AttributeConfig single) {
      return List.of(single);
    }

    if (modifier.getConfig() instanceof MultiplyAttributeConfig multi) {
      return multi.instances();
    }

    return List.of();
  }

  public static boolean hasGemstoneAttributeModifiers(ItemStack itemStack) {
    AttributeModifiersComponent component = itemStack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
    if (component == null) {
      return false;
    }

    return component.modifiers().stream()
        .anyMatch(e -> e.modifier().id() != null
            && e.modifier().id().getNamespace().equalsIgnoreCase(Gemstones.MOD_ID));
  }
}
