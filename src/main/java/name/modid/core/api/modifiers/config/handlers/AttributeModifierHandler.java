package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import name.modid.Gemstones;
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

  @SuppressWarnings("deprecation")
  public static void apply(ArrayList<GemstoneModifier> gemstoneModifiers, ItemStack itemStack) {
    Item item = itemStack.getItem();

    AttributeModifiersComponent baseModifiers = item.getAttributeModifiers();
    AttributeModifiersComponent customModifiers = itemStack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT);

    Map<String, AttributeModifiersComponent.Entry> combinedModifiersMap = new LinkedHashMap<>();
    Function<AttributeModifiersComponent.Entry, String> entryKey = e -> String.format("%s.%s.%s", e.modifier().id(),
        e.slot(), e.attribute().value());

    // Split and flat modifiers and merge in one array
    baseModifiers.modifiers().forEach(e -> {
      if (e.modifier().id() != null && !e.modifier().id().getNamespace().equalsIgnoreCase(Gemstones.MOD_ID)) {
        combinedModifiersMap.put(entryKey.apply(e), e);
      }
    });

    customModifiers.modifiers().forEach(e -> {
      if (e.modifier().id() != null && !e.modifier().id().getNamespace().equalsIgnoreCase(Gemstones.MOD_ID)) {
        combinedModifiersMap.put(entryKey.apply(e), e);
      }
    });

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

      AttributeModifiersComponent.Entry newEntry = new AttributeModifiersComponent.Entry(
          attribute,
          scaledGemstoneModifier,
          ModifierHelper.getAttributeModifierSlot(item));

      combinedModifiersMap.put(entryKey.apply(newEntry), newEntry);
    }

    List<AttributeModifiersComponent.Entry> finalModifiers = new ArrayList<>(combinedModifiersMap.values());
    if (finalModifiers.isEmpty()) {
      itemStack.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      return;
    }

    AttributeModifiersComponent finalComponent = new AttributeModifiersComponent(finalModifiers, true);
    itemStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, finalComponent);
  }
}
