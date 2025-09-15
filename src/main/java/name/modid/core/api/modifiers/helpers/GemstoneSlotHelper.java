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
import name.modid.core.api.components.Gemstone;
import name.modid.core.api.components.GemstoneSlots;
import name.modid.core.api.modifiers.categories.ModifierAttribute;
import name.modid.core.api.modifiers.categories.ModifierMultiplyAttribute;
import name.modid.core.api.modifiers.config.GemstoneModifier;
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

  public static ArrayList<Gemstone> contains(ItemStack itemStack, GemstoneType gemstoneType) {
    ArrayList<Gemstone> gemstones = itemStack.get(ComponentsRegistry.GEMSTONES) != null
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

  public static GemstoneSlots getGemstonesSlot(ItemStack itemStack) {
    return itemStack.get(ComponentsRegistry.GEMSTONES);
  }

  public static Gemstone[] getGemstones(ItemStack itemStack) {
    if (itemStack == null || itemStack.isEmpty()) {
      return new Gemstone[0];
    }

    GemstoneSlots slots = itemStack.get(ComponentsRegistry.GEMSTONES);
    return slots != null ? slots.gemstones() : new Gemstone[0];
  }

  public static Integer getFirstEmptySlotIndex(ItemStack itemStack) {
    GemstoneSlots gemstoneSlots = getGemstonesSlot(itemStack);
    if (gemstoneSlots == null) {
      return null;
    }

    Gemstone[] gemstones = gemstoneSlots.gemstones();
    if (gemstones == null) {
      return null;
    }

    for (int i = 0; i < gemstones.length; i++) {
      Gemstone gemstone = gemstones[i];
      if (gemstone != null && gemstone.gemstoneType() == GemstoneType.EMPTY) {
        return i;
      }
    }

    return null;
  }

  public static ItemStack setGemstoneByIndex(ItemStack itemStack, int index, GemstoneItem gemstone) {
    GemstoneSlots sourceGemstoneSlots = getGemstonesSlot(itemStack);
    if (sourceGemstoneSlots == null || index < 0 || index >= MAX_SLOTS) {
      return null;
    }

    Gemstone[] gemstones = Arrays.copyOf(sourceGemstoneSlots.gemstones(), sourceGemstoneSlots.gemstones().length);

    gemstones[index] = new Gemstone(
        gemstone.getType(),
        gemstone.getRarityType());

    itemStack.set(ComponentsRegistry.GEMSTONES, new GemstoneSlots(gemstones));
    updateItemSlotBonuses(itemStack, itemStack.getItem());

    return itemStack;
  }

  public static void initItemSlots(ItemStack itemStack, Item item) {
    if (!isItemValid(item))
      return;

    GemstoneSlots currentSlots = itemStack.get(ComponentsRegistry.GEMSTONES);
    if (currentSlots == null || currentSlots.gemstones().length != MAX_SLOTS) {
      Gemstone[] gemstones = new Gemstone[MAX_SLOTS];

      int freeSlots = 1 + new Random().nextInt(2);

      for (int i = 0; i < MAX_SLOTS; i++) {
        if (freeSlots != 0) {
          gemstones[i] = new Gemstone(GemstoneType.EMPTY, GemstoneQuality.NONE);
          freeSlots--;
        } else {
          gemstones[i] = new Gemstone(GemstoneType.LOCKED, GemstoneQuality.NONE);
        }
      }

      itemStack.set(ComponentsRegistry.GEMSTONES, new GemstoneSlots(gemstones));
      updateItemSlotBonuses(itemStack, item);
    }
  }

  public static void updateItemSlotBonuses(ItemStack itemStack, Item item) {
    if (!isItemValid(item) && !isGemstonesExists(itemStack))
      return;

    Gemstone[] gemstones = getGemstones(itemStack);
    if (gemstones == null)
      return;

    ArrayList<ModifierAttribute> modifiers = ModifierGatheringHelper.getAttributeModifiers(itemStack);

    applyAttributeModifiers(modifiers, item, itemStack);
  }

  public static void applyAttributeModifiers(ArrayList<ModifierAttribute> gemstoneModifiers,
      Item item, ItemStack itemStack) {
    @SuppressWarnings("deprecation")
    AttributeModifiersComponent baseModifiers = itemStack.getItem().getAttributeModifiers();
    AttributeModifiersComponent customModifiers = itemStack
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

    // Using LinkedHashMap for attributes order and eliminate possible duplicates
    Map<String, AttributeModifiersComponent.Entry> combinedModifiersMap = new LinkedHashMap<>();
    Function<AttributeModifiersComponent.Entry, String> entryKey = entry -> entry.modifier().id().toString() + "."
        + entry.slot() + "."
        + entry.attribute().value();

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
    Map<RegistryEntry<EntityAttribute>, List<ModifierAttribute>> attributeToModifiers = new HashMap<>();
    for (GemstoneModifier modifier : gemstoneModifiers) {
      if (modifier instanceof ModifierAttribute singleModifier) {
        attributeToModifiers.computeIfAbsent(singleModifier.getAttributeEntry(), k -> new ArrayList<>())
            .add(singleModifier);
      } else if (modifier instanceof ModifierMultiplyAttribute multiModifier) {
        for (ModifierAttribute attr : multiModifier.getInstances()) {
          attributeToModifiers.computeIfAbsent(attr.getAttributeEntry(), k -> new ArrayList<>()).add(attr);
        }
      }
    }

    for (Map.Entry<RegistryEntry<EntityAttribute>, List<ModifierAttribute>> modifierEntry : attributeToModifiers
        .entrySet()) {
      RegistryEntry<EntityAttribute> attribute = modifierEntry.getKey();
      List<ModifierAttribute> modifiers = modifierEntry.getValue();
      ModifierAttribute mod = modifiers.get(0);

      float totalValue = 0f;
      for (ModifierAttribute m : modifiers) {
        GemstoneQuality rarity = m.getRarityType();
        totalValue += m.getLevelValues().get(rarity);
      }

      EquipmentSlot slot = ModifierHelper.getEquipmentSlot(item);

      Identifier modifierId = Identifier.of(Gemstones.MOD_ID,
          String.format("%s.%s.%s", mod.getGemstoneType().toString().toLowerCase(),
              mod.getItemCategory().toString().toLowerCase(), slot.name().toLowerCase()));

      EntityAttributeModifier scaledGemstoneModifier = new EntityAttributeModifier(modifierId, totalValue,
          mod.getOperation());

      AttributeModifiersComponent.Entry newEntry = new AttributeModifiersComponent.Entry(attribute,
          scaledGemstoneModifier, ModifierHelper.getAttributeModifierSlot(item));
      combinedModifiersMap.put(entryKey.apply(newEntry), newEntry);
    }

    List<AttributeModifiersComponent.Entry> finalModifiers = new ArrayList<>(combinedModifiersMap.values());

    AttributeModifiersComponent finalComponent = new AttributeModifiersComponent(finalModifiers, true);
    itemStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, finalComponent);
  }
}
