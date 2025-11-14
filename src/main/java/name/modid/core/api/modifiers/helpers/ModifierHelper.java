package name.modid.core.api.modifiers.helpers;

import java.util.Map;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
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
import net.minecraft.item.ToolItem;

public class ModifierHelper {
  public static Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> getGemstoneModifiers(
      GemstoneType gemstoneType, Item item) {
    if (gemstoneType == GemstoneType.EMPTY || gemstoneType == GemstoneType.LOCKED) {
      return null;
    }

    ModifiersData modifiersData = ModifiersRegistry.MODIFIER_REGISTRY().get(gemstoneType);
    return modifiersData.getModifiers();
  }

  public static GemstoneModifier getGemstoneModifierForItem(
      GemstoneType gemstoneType, GemstoneQuality GemstoneQualityType, Item item) {
    if (gemstoneType == GemstoneType.EMPTY || gemstoneType == GemstoneType.LOCKED) {
      return null;
    }

    Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> modifiers = ModifiersRegistry
        .getModifiersForGemstone(gemstoneType);

    ModifierItemCategory category = getModifieritemSlot(item);
    Map<GemstoneQuality, GemstoneModifier> rarityMap = modifiers.get(category);

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
    } else if (item instanceof SwordItem || item instanceof ToolItem) {
      return EquipmentSlot.MAINHAND;
    } else {
      return EquipmentSlot.MAINHAND;
    }
  }

  public static ModifierItemCategory getModifieritemSlot(Item item) {
    if (item instanceof SwordItem) {
      return ModifierItemCategory.MELEE;
    }
    if (item instanceof BowItem || item instanceof CrossbowItem) {
      return ModifierItemCategory.RANGED;
    }
    if (item instanceof AxeItem || item instanceof PickaxeItem || item instanceof ShovelItem) {
      return ModifierItemCategory.TOOLS;
    }
    if (item instanceof ArmorItem) {
      return ModifierItemCategory.ARMOR;
    }
    return ModifierItemCategory.TOOLS;
  }
}