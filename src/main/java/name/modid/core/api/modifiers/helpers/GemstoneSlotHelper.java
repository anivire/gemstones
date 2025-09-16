package name.modid.core.api.modifiers.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import name.modid.Gemstones;
import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.components.GemstoneSlotsComponent;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.AttributeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.MultiplyAttributeConfig;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.content.items.GemstoneItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class GemstoneSlotHelper {
  public static final int MAX_SLOTS = 5;

  public static ArrayList<GemstoneComponent> contains(ItemStack itemStack, GemstoneType gemstoneType) {
    ArrayList<GemstoneComponent> gemstones = itemStack.get(ComponentsRegistry.GEMSTONES) != null
        ? new ArrayList<>(Arrays.asList(itemStack.get(ComponentsRegistry.GEMSTONES).gemstones()))
        : new ArrayList<>();

    gemstones.removeIf(g -> g.gemstoneType() != gemstoneType);
    return gemstones;
  }

  public static boolean isItemValid(Item item) {
    return item instanceof PickaxeItem || item instanceof BowItem || item instanceof ArmorItem
        || item instanceof SwordItem || item instanceof AxeItem || item instanceof ShovelItem
        || item instanceof CrossbowItem;
  }

  public static boolean isGemstonesExists(ItemStack itemStack) {
    return itemStack.get(ComponentsRegistry.GEMSTONES) != null;
  }

  public static GemstoneSlotsComponent getGemstonesSlot(ItemStack itemStack) {
    return itemStack.get(ComponentsRegistry.GEMSTONES);
  }

  public static GemstoneComponent[] getGemstones(ItemStack itemStack) {
    if (itemStack == null || itemStack.isEmpty()) {
      return new GemstoneComponent[0];
    }

    GemstoneSlotsComponent slots = itemStack.get(ComponentsRegistry.GEMSTONES);
    return slots != null ? slots.gemstones() : new GemstoneComponent[0];
  }

  public static Integer getFirstEmptySlotIndex(ItemStack itemStack) {
    GemstoneSlotsComponent gemstoneSlots = getGemstonesSlot(itemStack);
    if (gemstoneSlots == null) {
      return null;
    }

    GemstoneComponent[] gemstones = gemstoneSlots.gemstones();
    if (gemstones == null) {
      return null;
    }

    for (int i = 0; i < gemstones.length; i++) {
      GemstoneComponent gemstone = gemstones[i];
      if (gemstone != null && gemstone.gemstoneType() == GemstoneType.EMPTY) {
        return i;
      }
    }

    return null;
  }

  public static ItemStack setGemstoneByIndex(ItemStack itemStack, int index, GemstoneItem gemstone) {
    GemstoneSlotsComponent sourceGemstoneSlots = getGemstonesSlot(itemStack);
    if (sourceGemstoneSlots == null || index < 0 || index >= MAX_SLOTS) {
      return null;
    }

    GemstoneComponent[] gemstones = Arrays.copyOf(sourceGemstoneSlots.gemstones(),
        sourceGemstoneSlots.gemstones().length);

    gemstones[index] = new GemstoneComponent(
        gemstone.getType(),
        gemstone.getRarityType());

    itemStack.set(ComponentsRegistry.GEMSTONES, new GemstoneSlotsComponent(gemstones));
    updateSocketsAttributes(itemStack, itemStack.getItem());

    return itemStack;
  }

  public static void initializeSockets(ItemStack itemStack, Item item) {
    if (!isItemValid(item)) {
      return;
    }

    GemstoneSlotsComponent currentSlots = itemStack.get(ComponentsRegistry.GEMSTONES);
    if (currentSlots == null || currentSlots.gemstones().length != MAX_SLOTS) {
      GemstoneComponent[] gemstones = new GemstoneComponent[MAX_SLOTS];

      int freeSlots = 1 + new Random().nextInt(2);

      for (int i = 0; i < MAX_SLOTS; i++) {
        if (freeSlots != 0) {
          gemstones[i] = new GemstoneComponent(GemstoneType.EMPTY, GemstoneQuality.NONE);
          freeSlots--;
        } else {
          gemstones[i] = new GemstoneComponent(GemstoneType.LOCKED, GemstoneQuality.NONE);
        }
      }

      itemStack.set(ComponentsRegistry.GEMSTONES, new GemstoneSlotsComponent(gemstones));
      updateSocketsAttributes(itemStack, item);
    }
  }

  public static void updateSocketsAttributes(ItemStack itemStack, Item item) {
    GemstoneComponent[] gemstones = getGemstones(itemStack);
    if (gemstones == null) {
      return;
    }

    ArrayList<GemstoneModifier> modifiers = ModifierGatheringHelper.getAttributeModifiers(itemStack);

    applyAttributeModifiers(modifiers, item, itemStack);
  }

  public static void applyAttributeModifiers(ArrayList<GemstoneModifier> gemstoneModifiers,
      Item item, ItemStack itemStack) {
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
