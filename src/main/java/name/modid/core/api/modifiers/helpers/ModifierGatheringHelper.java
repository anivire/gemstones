package name.modid.core.api.modifiers.helpers;

import java.util.ArrayList;

import name.modid.core.api.components.Gemstone;
import name.modid.core.api.modifiers.categories.ModifierAreaEffect;
import name.modid.core.api.modifiers.categories.ModifierAttribute;
import name.modid.core.api.modifiers.categories.ModifierCustomCondition;
import name.modid.core.api.modifiers.categories.ModifierMultiplyAttribute;
import name.modid.core.api.modifiers.categories.ModifierOnBlockBreak;
import name.modid.core.api.modifiers.categories.ModifierOnDamage;
import name.modid.core.api.modifiers.categories.ModifierOnFirstHitMelee;
import name.modid.core.api.modifiers.categories.ModifierOnHitEffectMelee;
import name.modid.core.api.modifiers.categories.ModifierOnHitEffectProjectile;
import name.modid.core.api.modifiers.categories.ModifierOnHitMelee;
import name.modid.core.api.modifiers.categories.ModifierOnHitProjectile;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.types.GemstoneType;
import net.minecraft.item.ItemStack;

public class ModifierGatheringHelper {
  public static ArrayList<ModifierAttribute> getAttributeModifiers(ItemStack itemStack) {
    ArrayList<ModifierAttribute> modifiers = new ArrayList<>();
    for (Gemstone gem : GemstoneSlotHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(gem.gemstoneType(),
            gem.GemstoneQualityType(),
            itemStack.getItem());
        if (modifier instanceof ModifierAttribute m) {
          modifiers.add(m);
        } else if (modifier instanceof ModifierMultiplyAttribute multi) {
          modifiers.addAll(multi.getInstances());
        }
      }
    }
    return modifiers;
  }

  public static ArrayList<ModifierOnHitProjectile> getOnHitProjectileModifiers(ItemStack itemStack) {
    ArrayList<ModifierOnHitProjectile> modifiers = new ArrayList<>();
    for (Gemstone gem : GemstoneSlotHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(gem.gemstoneType(),
            gem.GemstoneQualityType(),
            itemStack.getItem());
        if (modifier instanceof ModifierOnHitProjectile m) {
          modifiers.add(m);
        }
      }
    }
    return modifiers;
  }

  public static ArrayList<ModifierOnHitMelee> getOnHitMeleeModifiers(ItemStack itemStack) {
    ArrayList<ModifierOnHitMelee> modifiers = new ArrayList<>();
    for (Gemstone gem : GemstoneSlotHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(gem.gemstoneType(),
            gem.GemstoneQualityType(),
            itemStack.getItem());
        if (modifier instanceof ModifierOnHitMelee m) {
          modifiers.add(m);
        }
      }
    }
    return modifiers;
  }

  public static ArrayList<ModifierOnFirstHitMelee> getOnHitFirstModifiers(ItemStack itemStack) {
    ArrayList<ModifierOnFirstHitMelee> modifiers = new ArrayList<>();
    for (Gemstone gem : GemstoneSlotHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(gem.gemstoneType(),
            gem.GemstoneQualityType(),
            itemStack.getItem());
        if (modifier instanceof ModifierOnFirstHitMelee m) {
          modifiers.add(m);
        }
      }
    }
    return modifiers;
  }

  public static ArrayList<ModifierOnHitEffectMelee> getOnHitEffectModifiers(ItemStack itemStack) {
    ArrayList<ModifierOnHitEffectMelee> modifiers = new ArrayList<>();
    for (Gemstone gem : GemstoneSlotHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(gem.gemstoneType(),
            gem.GemstoneQualityType(),
            itemStack.getItem());
        if (modifier instanceof ModifierOnHitEffectMelee m) {
          modifiers.add(m);
        }
      }
    }
    return modifiers;
  }

  public static ArrayList<ModifierAreaEffect> getAreaEffectModifiers(ItemStack itemStack) {
    ArrayList<ModifierAreaEffect> modifiers = new ArrayList<>();
    for (Gemstone gem : GemstoneSlotHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(gem.gemstoneType(),
            gem.GemstoneQualityType(),
            itemStack.getItem());
        if (modifier instanceof ModifierAreaEffect m) {
          modifiers.add(m);
        }
      }
    }
    return modifiers;
  }

  public static ArrayList<ModifierOnHitEffectProjectile> getOnHitEffectProjectileModifiers(
      ItemStack itemStack) {
    ArrayList<ModifierOnHitEffectProjectile> modifiers = new ArrayList<>();
    for (Gemstone gem : GemstoneSlotHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(gem.gemstoneType(),
            gem.GemstoneQualityType(),
            itemStack.getItem());
        if (modifier instanceof ModifierOnHitEffectProjectile m) {
          modifiers.add(m);
        }
      }
    }
    return modifiers;
  }

  public static ArrayList<ModifierOnBlockBreak> getOnBlockBreakModifiers(ItemStack itemStack) {
    ArrayList<ModifierOnBlockBreak> modifiers = new ArrayList<>();
    for (Gemstone gem : GemstoneSlotHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(gem.gemstoneType(),
            gem.GemstoneQualityType(),
            itemStack.getItem());
        if (modifier instanceof ModifierOnBlockBreak m) {
          modifiers.add(m);
        }
      }
    }
    return modifiers;
  }

  public static ArrayList<ModifierOnDamage> getOnDamageModifiers(ItemStack itemStack) {
    ArrayList<ModifierOnDamage> modifiers = new ArrayList<>();
    for (Gemstone gem : GemstoneSlotHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(gem.gemstoneType(),
            gem.GemstoneQualityType(),
            itemStack.getItem());
        if (modifier instanceof ModifierOnDamage m) {
          modifiers.add(m);
        }
      }
    }
    return modifiers;
  }

  public static ArrayList<ModifierCustomCondition> getCustomConditionModifiers(ItemStack itemStack) {
    ArrayList<ModifierCustomCondition> modifiers = new ArrayList<>();
    for (Gemstone gem : GemstoneSlotHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(gem.gemstoneType(),
            gem.GemstoneQualityType(),
            itemStack.getItem());
        if (modifier instanceof ModifierCustomCondition m) {
          modifiers.add(m);
        }
      }
    }
    return modifiers;
  }
}
