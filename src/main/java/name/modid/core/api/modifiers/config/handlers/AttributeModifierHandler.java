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
  public static void apply(ArrayList<GemstoneModifier> gemstoneModifiers, ItemStack itemStack) {
    Item item = itemStack.getItem();

    @SuppressWarnings("deprecation")
    AttributeModifiersComponent baseModifiers = itemStack.getItem().getAttributeModifiers();
    AttributeModifiersComponent customModifiers = itemStack
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

    // Using LinkedHashMap for attributes order and eliminate possible duplicates
    Map<String, AttributeModifiersComponent.Entry> combinedModifiersMap = new LinkedHashMap<>();
    Function<AttributeModifiersComponent.Entry, String> entryKey = entry -> String.format("%s.%s.%s",
        entry.modifier().id().toString(), entry.slot(), entry.attribute().value());

    // Filter modifiers
    baseModifiers.modifiers().forEach(e -> {
      if (!e.modifier().id().getNamespace().equals(Gemstones.MOD_ID)) {
        combinedModifiersMap.put(entryKey.apply(e), e);
      }
    });
    customModifiers.modifiers().forEach(e -> {
      if (!e.modifier().id().getNamespace().equals(Gemstones.MOD_ID)) {
        combinedModifiersMap.put(entryKey.apply(e), e);
      }
    });

    // Gather modifiers
    Map<RegistryEntry<EntityAttribute>, List<GemstoneModifier>> attributeToModifiers = new HashMap<>();
    for (GemstoneModifier modifier : gemstoneModifiers) {
      if (modifier.getConfig() instanceof AttributeConfig singleModifier) {
        attributeToModifiers.computeIfAbsent(singleModifier.attribute(), k -> new ArrayList<>())
            .add(modifier);
      } else if (modifier.getConfig() instanceof MultiplyAttributeConfig multiModifier) {
        for (AttributeConfig c : multiModifier.instances()) {
          attributeToModifiers.computeIfAbsent(c.attribute(), k -> new ArrayList<>()).add(modifier);
        }
      }
    }

    for (Map.Entry<RegistryEntry<EntityAttribute>, List<GemstoneModifier>> modifierEntry : attributeToModifiers
        .entrySet()) {
      RegistryEntry<EntityAttribute> attribute = modifierEntry.getKey();
      List<GemstoneModifier> modifiers = modifierEntry.getValue();
      GemstoneModifier mod = modifiers.get(0);

      float totalValue = 0f;
      for (GemstoneModifier m : modifiers) {
        GemstoneQuality rarity = m.getRarityType();
        if (m.getConfig() instanceof AttributeConfig c) {
          totalValue += c.values().get(rarity);
        }
      }

      EquipmentSlot slot = ModifierHelper.getEquipmentSlot(item);

      Identifier modifierId = Identifier.of(Gemstones.MOD_ID,
          String.format("%s.%s.%s", mod.getGemstoneType().toString().toLowerCase(),
              mod.getItemCategory().toString().toLowerCase(), slot.name().toLowerCase()));

      EntityAttributeModifier scaledGemstoneModifier = new EntityAttributeModifier(modifierId, totalValue, null);
      if (mod.getConfig() instanceof AttributeConfig c) {
        scaledGemstoneModifier = new EntityAttributeModifier(modifierId, totalValue,
            c.operation());
      }

      AttributeModifiersComponent.Entry newEntry = new AttributeModifiersComponent.Entry(attribute,
          scaledGemstoneModifier, ModifierHelper.getAttributeModifierSlot(item));
      combinedModifiersMap.put(entryKey.apply(newEntry), newEntry);
    }

    List<AttributeModifiersComponent.Entry> finalModifiers = new ArrayList<>(combinedModifiersMap.values());

    AttributeModifiersComponent finalComponent = new AttributeModifiersComponent(finalModifiers, true);
    itemStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, finalComponent);
  }
}
