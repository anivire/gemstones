package name.modid.helpers.modifiers;

import java.util.ArrayList;
import java.util.Map;
import name.modid.helpers.ItemGemstoneHelper;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHit;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;

public class GemstoneModifierHelper {
  public static Map<GemstoneModifierItemType, GemstoneModifier> getGemstoneModifiers(
      GemstoneType gemstoneType, Item item) {
    if (gemstoneType == GemstoneType.EMPTY || gemstoneType == GemstoneType.LOCKED)
      return null;

    GemstonesModifierData modifiersData =
        GemstoneModifierRegistration.MODIFIER_REGISTRY().get(gemstoneType);
    Map<GemstoneModifierItemType, GemstoneModifier> modifiers = modifiersData.getModifiers();

    return modifiers;
  }

  public static GemstoneModifier getGemstoneModifierForItem(GemstoneType gemstoneType, Item item) {
    if (gemstoneType == GemstoneType.EMPTY || gemstoneType == GemstoneType.LOCKED)
      return null;

    GemstonesModifierData modifiersData =
        GemstoneModifierRegistration.MODIFIER_REGISTRY().get(gemstoneType);
    Map<GemstoneModifierItemType, GemstoneModifier> modifiers = modifiersData.getModifiers();
    GemstoneModifier modifier = modifiers.get(getModifieritemSlot(item));

    return modifier;
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

  public static GemstoneModifierItemType getModifieritemSlot(Item item) {
    if (item instanceof SwordItem) {
      return GemstoneModifierItemType.MELEE;
    }

    if (item instanceof BowItem || item instanceof CrossbowItem) {
      return GemstoneModifierItemType.RANGED;
    }

    if (item instanceof AxeItem || item instanceof PickaxeItem || item instanceof ShovelItem) {
      return GemstoneModifierItemType.TOOLS;
    }

    if (item instanceof ArmorItem) {
      return GemstoneModifierItemType.ARMOR;
    }

    return GemstoneModifierItemType.TOOLS;
  }

  public static ArrayList<ModifierOnHit> getOnHitModifiers(ItemStack itemStack) {
    ArrayList<ModifierOnHit> modifiers = new ArrayList<>();
    Gemstone[] gemstones = ItemGemstoneHelper.getGemstones(itemStack);

    for (Gemstone gem : gemstones) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier =
            getGemstoneModifierForItem(gem.gemstoneType(), itemStack.getItem());
        if (modifier instanceof ModifierOnHit modifierOnHit) {
          ModifierOnHit newModifier = new ModifierOnHit(modifierOnHit.eventChance,
              modifierOnHit.eventType, modifierOnHit.itemType, modifierOnHit.gemstoneType);
          newModifier.setRarityType(gem.gemstoneRarityType());
          modifiers.add(newModifier);
        }
      }
    }

    return modifiers;
  }

  public static ArrayList<ModifierOnHitEffect> getOnHitEffectModifiers(ItemStack itemStack) {
    ArrayList<ModifierOnHitEffect> modifiers = new ArrayList<>();
    Gemstone[] gemstones = ItemGemstoneHelper.getGemstones(itemStack);

    for (Gemstone gem : gemstones) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier =
            getGemstoneModifierForItem(gem.gemstoneType(), itemStack.getItem());
        if (modifier instanceof ModifierOnHitEffect modifierOnHitEffect) {
          ModifierOnHitEffect newModifier = new ModifierOnHitEffect(
              modifierOnHitEffect.inflitChance, modifierOnHitEffect.duration,
              modifierOnHitEffect.amplifier, modifierOnHitEffect.itemType,
              modifierOnHitEffect.effect, modifierOnHitEffect.isStacking,
              modifierOnHitEffect.maxStackCount, modifierOnHitEffect.gemstoneType);
          newModifier.setRarityType(gem.gemstoneRarityType());
          modifiers.add(newModifier);
        }
      }
    }

    return modifiers;
  }

  public static ArrayList<ModifierOnBlockBreak> getOnBlockBreakModifiers(ItemStack itemStack) {
    ArrayList<ModifierOnBlockBreak> modifiers = new ArrayList<>();
    Gemstone[] gemstones = ItemGemstoneHelper.getGemstones(itemStack);

    for (Gemstone gem : gemstones) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier =
            getGemstoneModifierForItem(gem.gemstoneType(), itemStack.getItem());
        if (modifier instanceof ModifierOnBlockBreak modifierOnBlockBreak) {
          ModifierOnBlockBreak newModifier = new ModifierOnBlockBreak(modifierOnBlockBreak.value,
              modifierOnBlockBreak.additionalValue, modifierOnBlockBreak.itemType,
              modifierOnBlockBreak.eventType, modifierOnBlockBreak.gemstoneType);
          newModifier.setRarityType(gem.gemstoneRarityType());
          modifiers.add(newModifier);
        }
      }
    }

    return modifiers;
  }
}
