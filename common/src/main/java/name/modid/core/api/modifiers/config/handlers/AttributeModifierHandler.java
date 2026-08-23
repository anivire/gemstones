package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.modid.Gemstones;
import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.AttributeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.MultiplyAttributeConfig;
import name.modid.core.api.modifiers.helpers.ModifierHelper;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class AttributeModifierHandler {

  public static void apply(ArrayList<GemstoneModifier> gemstoneModifiers, ItemStack itemStack) {
    Item item = itemStack.getItem();

    // Collect gemstone modifiers
    Map<RegistryEntry<EntityAttribute>, List<GemstoneModifier>> attributeToModifiers = new HashMap<>();
    for (GemstoneModifier modifier : gemstoneModifiers) {
      if (modifier.getConfig() instanceof AttributeConfig single) {
        attributeToModifiers.computeIfAbsent(single.attribute(), k -> new ArrayList<>()).add(modifier);
      } else if (modifier.getConfig() instanceof MultiplyAttributeConfig multi) {
        for (AttributeConfig c : multi.instances()) {
          attributeToModifiers.computeIfAbsent(c.attribute(), k -> new ArrayList<>()).add(modifier);
        }
      }
    }

    // Collect gemstone modifiers attrs
    List<AttributeModifiersComponent.Entry> gemstoneEntries = new ArrayList<>();
    for (Map.Entry<RegistryEntry<EntityAttribute>, List<GemstoneModifier>> entry : attributeToModifiers.entrySet()) {
      RegistryEntry<EntityAttribute> attribute = entry.getKey();
      List<GemstoneModifier> modifiers = entry.getValue();
      float totalValue = 0f;
      AttributeConfig firstConfig = null;
      GemstoneModifier firstModifier = null;
      for (GemstoneModifier m : modifiers) {
        GemstoneQuality rarity = m.getRarityType();
        if (m.getConfig() instanceof AttributeConfig c) {
          totalValue += c.values().get(rarity);
          if (firstConfig == null) {
            firstConfig = c;
            firstModifier = m;
          }
        } else if (m.getConfig() instanceof MultiplyAttributeConfig multi) {
          for (AttributeConfig c : multi.instances()) {
            if (c.attribute().equals(attribute)) {
              totalValue += c.values().get(rarity);
              if (firstConfig == null) {
                firstConfig = c;
                firstModifier = m;
              }
            }
          }
        }
      }

      if (firstConfig == null || firstModifier == null) {
        continue;
      }

      EquipmentSlot slot = ModifierHelper.getEquipmentSlot(item);
      Identifier modifierId = Identifier.of(Gemstones.MOD_ID,
          String.format("%s.%s.%s",
              firstModifier.getGemstoneType().toString().toLowerCase(),
              firstModifier.getItemCategory().toString().toLowerCase(),
              slot.name().toLowerCase()));

      EntityAttributeModifier scaledGemstoneModifier = new EntityAttributeModifier(
          modifierId,
          (double) totalValue,
          firstConfig.operation());

      gemstoneEntries.add(new AttributeModifiersComponent.Entry(
          attribute,
          scaledGemstoneModifier,
          ModifierHelper.getAttributeModifierSlot(item)));
    }

    // Gemstone modifiers live in a dedicated component. This keeps separated
    // ATTRIBUTE_MODIFIERS component from vanilla and other mods
    if (gemstoneEntries.isEmpty()) {
      itemStack.remove(ComponentsRegistry.gemstoneAttributeModifiers());
      removeLegacyGemstoneModifiers(itemStack);
      return;
    }

    itemStack.set(ComponentsRegistry.gemstoneAttributeModifiers(),
        new AttributeModifiersComponent(gemstoneEntries, true));
    removeLegacyGemstoneModifiers(itemStack);
  }

  public static boolean hasGemstoneAttributeModifiers(ItemStack itemStack) {
    AttributeModifiersComponent gemstoneComponent = itemStack.get(ComponentsRegistry.gemstoneAttributeModifiers());
    if (gemstoneComponent != null && !gemstoneComponent.modifiers().isEmpty()) {
      return true;
    }

    AttributeModifiersComponent component = itemStack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
    if (component == null) {
      return false;
    }

    return component.modifiers().stream()
        .anyMatch(e -> isGemstoneModifierId(e.modifier().id()));
  }

  static boolean isGemstoneModifierId(Identifier modifierId) {
    return modifierId != null && modifierId.getNamespace().equalsIgnoreCase(Gemstones.MOD_ID);
  }

  // Legacy support for gemstone modifiers that were previously stored in
  // vanilla ATTRIBUTE_MODIFIERS component
  private static void removeLegacyGemstoneModifiers(ItemStack itemStack) {
    AttributeModifiersComponent legacyComponent = itemStack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
    if (legacyComponent == null || legacyComponent.modifiers().stream()
        .noneMatch(entry -> isGemstoneModifierId(entry.modifier().id()))) {
      return;
    }

    List<AttributeModifiersComponent.Entry> remaining = legacyComponent.modifiers().stream()
        .filter(entry -> !isGemstoneModifierId(entry.modifier().id()))
        .toList();
    if (remaining.isEmpty()) {
      itemStack.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);
    } else {
      itemStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS,
          new AttributeModifiersComponent(remaining, legacyComponent.showInTooltip()));
    }
  }
}
