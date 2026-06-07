package name.modid.core.api.modifiers.helpers;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.datapack.items.ItemCompatibilityRegistry;
import name.modid.datapack.modifiers.ModifiersData;
import name.modid.datapack.modifiers.ModifiersRegistry;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;

public class ModifierHelper {
  private record ItemCategoryRule(Predicate<Item> predicate, ModifierItemCategory category) {
  }

  private static final ItemCategoryRule[] DEFAULT_ITEM_CATEGORY_RULES = {
      new ItemCategoryRule(item -> item instanceof SwordItem, ModifierItemCategory.MELEE),
      new ItemCategoryRule(item -> item instanceof BowItem || item instanceof CrossbowItem,
          ModifierItemCategory.RANGED),
      new ItemCategoryRule(item -> item instanceof AxeItem || item instanceof PickaxeItem || item instanceof ShovelItem,
          ModifierItemCategory.TOOLS),
      new ItemCategoryRule(item -> item instanceof ArmorItem, ModifierItemCategory.ARMOR)
  };

  public static Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> getGemstoneModifiers(
      GemstoneType gemstoneType, Item item) {
    if (gemstoneType == GemstoneType.EMPTY
        || gemstoneType == GemstoneType.LOCKED
        || gemstoneType == GemstoneType.UNDEFINED) {
      return null;
    }

    ModifiersData modifiersData = ModifiersRegistry.MODIFIER_REGISTRY().get(gemstoneType);
    return modifiersData.getModifiers();
  }

  public static GemstoneModifier getGemstoneModifierForItem(
      GemstoneType gemstoneType, GemstoneQuality GemstoneQualityType, Item item) {
    if (gemstoneType == GemstoneType.EMPTY
        || gemstoneType == GemstoneType.LOCKED
        || gemstoneType == GemstoneType.UNDEFINED) {
      return null;
    }

    Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> modifiers = ModifiersRegistry
        .getModifiersForGemstone(gemstoneType);

    Optional<ModifierItemCategory> category = getItemCategory(item);
    if (category.isEmpty()) {
      return null;
    }

    Map<GemstoneQuality, GemstoneModifier> rarityMap = modifiers.get(category.get());
    if (rarityMap == null) {
      rarityMap = modifiers.get(ModifierItemCategory.ALL);
    }

    if (rarityMap == null)
      return null;

    return rarityMap.get(GemstoneQualityType);
  }

  public static AttributeModifierSlot getAttributeModifierSlot(Item item) {
    if (item instanceof ArmorItem armorItem) {
      return switch (armorItem.getSlotType()) {
        case HEAD -> AttributeModifierSlot.HEAD;
        case CHEST -> AttributeModifierSlot.CHEST;
        case LEGS -> AttributeModifierSlot.LEGS;
        case FEET -> AttributeModifierSlot.FEET;
        default -> AttributeModifierSlot.CHEST;
      };
    }
    return AttributeModifierSlot.MAINHAND;
  }

  public static EquipmentSlot getEquipmentSlot(Item item) {
    if (item instanceof ArmorItem armorItem) {
      return armorItem.getSlotType();
    }
    return EquipmentSlot.MAINHAND;
  }

  public static ModifierItemCategory getModifieritemSlot(Item item) {
    return getItemCategory(item).orElse(ModifierItemCategory.TOOLS);
  }

  public static Optional<ModifierItemCategory> getItemCategory(Item item) {
    if (ItemCompatibilityRegistry.isBlacklisted(item)) {
      return Optional.empty();
    }

    var configuredCategory = ItemCompatibilityRegistry.getConfiguredCategory(item);
    if (configuredCategory.isPresent()) {
      return configuredCategory;
    }

    // if (item instanceof AnimalArmorItem) {
    // return Optional.empty();
    // }

    for (ItemCategoryRule rule : DEFAULT_ITEM_CATEGORY_RULES) {
      if (rule.predicate().test(item)) {
        return Optional.of(rule.category());
      }
    }

    return Optional.empty();
  }
}
