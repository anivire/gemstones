package name.modid.helpers.modifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.modid.helpers.GemstoneSocketingHelper;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.modifiers.modifierTypes.ModifierAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierCustomCondition;
import name.modid.helpers.modifiers.modifierTypes.ModifierMultiplyAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnDamage;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHit;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffectProjectile;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;

public class ModifierHelper {
  public static Map<ModifierItemCaregory, GemstoneModifier> getGemstoneModifiers(
      GemstoneType gemstoneType, Item item) {
    if (gemstoneType == GemstoneType.EMPTY || gemstoneType == GemstoneType.LOCKED)
      return null;

    ModifierData modifiersData = ModifierRegistration.MODIFIER_REGISTRY().get(gemstoneType);
    Map<ModifierItemCaregory, GemstoneModifier> modifiers = modifiersData.getModifiers();

    return modifiers;
  }

  public static GemstoneModifier getGemstoneModifierForItem(GemstoneType gemstoneType, Item item) {
    if (gemstoneType == GemstoneType.EMPTY || gemstoneType == GemstoneType.LOCKED)
      return null;

    ModifierData modifiersData = ModifierRegistration.MODIFIER_REGISTRY().get(gemstoneType);
    Map<ModifierItemCaregory, GemstoneModifier> modifiers = modifiersData.getModifiers();
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

  public static EquipmentSlot getEquipmentSlot(Item item) {
    if (item instanceof ArmorItem armorItem) {
      return armorItem.getSlotType();
    } else if (item instanceof SwordItem || item instanceof ToolItem) {
      return EquipmentSlot.MAINHAND;
    } else {
      return EquipmentSlot.MAINHAND;
    }
  }

  public static ModifierItemCaregory getModifieritemSlot(Item item) {
    if (item instanceof SwordItem) {
      return ModifierItemCaregory.MELEE;
    }

    if (item instanceof BowItem || item instanceof CrossbowItem) {
      return ModifierItemCaregory.RANGED;
    }

    if (item instanceof AxeItem || item instanceof PickaxeItem || item instanceof ShovelItem) {
      return ModifierItemCaregory.TOOLS;
    }

    if (item instanceof ArmorItem) {
      return ModifierItemCaregory.ARMOR;
    }

    return ModifierItemCaregory.TOOLS;
  }

  public static ArrayList<ModifierAttribute> getAttributeModifiers(ItemStack itemStack) {
    ArrayList<ModifierAttribute> modifiers = new ArrayList<>();
    Gemstone[] gemstones = GemstoneSocketingHelper.getGemstones(itemStack);

    for (Gemstone gem : gemstones) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), itemStack.getItem());
        if (modifier instanceof ModifierAttribute m) {
          ModifierAttribute inst = new ModifierAttribute(m.operation, m.modifierValuesList,
              m.itemType, m.attr, m.gemstoneType);
          inst.setRarityType(gem.gemstoneRarityType());

          modifiers.add(inst);
        } else if (modifier instanceof ModifierMultiplyAttribute m) {
          List<ModifierAttribute> instances = m.instances;

          for (ModifierAttribute i : instances) {
            ModifierAttribute inst = new ModifierAttribute(i.operation, i.modifierValuesList,
                i.itemType, i.attr, i.gemstoneType);
            inst.setRarityType(gem.gemstoneRarityType());

            modifiers.add(inst);
          }
        }
      }
    }

    return modifiers;
  }

  public static ArrayList<ModifierOnHit> getOnHitModifiers(ItemStack itemStack) {
    ArrayList<ModifierOnHit> modifiers = new ArrayList<>();
    Gemstone[] gemstones = GemstoneSocketingHelper.getGemstones(itemStack);

    for (Gemstone gem : gemstones) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), itemStack.getItem());
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
    Gemstone[] gemstones = GemstoneSocketingHelper.getGemstones(itemStack);

    for (Gemstone gem : gemstones) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), itemStack.getItem());
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

  public static ArrayList<ModifierOnHitEffectProjectile> getOnHitEffectProjectileModifiers(
      ItemStack itemStack) {
    ArrayList<ModifierOnHitEffectProjectile> modifiers = new ArrayList<>();
    Gemstone[] gemstones = GemstoneSocketingHelper.getGemstones(itemStack);

    for (Gemstone gem : gemstones) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), itemStack.getItem());
        if (modifier instanceof ModifierOnHitEffectProjectile modifierOnHitEffectProjectile) {
          ModifierOnHitEffectProjectile newModifier = new ModifierOnHitEffectProjectile(
              modifierOnHitEffectProjectile.inflitChance, modifierOnHitEffectProjectile.duration,
              modifierOnHitEffectProjectile.amplifier, modifierOnHitEffectProjectile.itemType,
              modifierOnHitEffectProjectile.effect, modifierOnHitEffectProjectile.isStacking,
              modifierOnHitEffectProjectile.maxStackCount,
              modifierOnHitEffectProjectile.gemstoneType);
          newModifier.setRarityType(gem.gemstoneRarityType());
          modifiers.add(newModifier);
        }
      }
    }

    return modifiers;
  }

  public static ArrayList<ModifierOnBlockBreak> getOnBlockBreakModifiers(ItemStack itemStack) {
    ArrayList<ModifierOnBlockBreak> modifiers = new ArrayList<>();
    Gemstone[] gemstones = GemstoneSocketingHelper.getGemstones(itemStack);

    for (Gemstone gem : gemstones) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), itemStack.getItem());
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

  public static ArrayList<ModifierOnDamage> getOnDamageModifiers(ItemStack itemStack) {
    ArrayList<ModifierOnDamage> modifiers = new ArrayList<>();
    Gemstone[] gemstones = GemstoneSocketingHelper.getGemstones(itemStack);

    for (Gemstone gem : gemstones) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), itemStack.getItem());
        if (modifier instanceof ModifierOnDamage modifierOnDamage) {
          ModifierOnDamage newModifier = new ModifierOnDamage(modifierOnDamage.value,
              modifierOnDamage.additionalValue, modifierOnDamage.eventType,
              modifierOnDamage.itemType, modifierOnDamage.gemstoneType);
          newModifier.setRarityType(gem.gemstoneRarityType());
          modifiers.add(newModifier);
        }
      }
    }

    return modifiers;
  }

  public static ArrayList<ModifierCustomCondition> getCustomConditionModifiers(ItemStack itemStack) {
    ArrayList<ModifierCustomCondition> modifiers = new ArrayList<>();
    Gemstone[] gemstones = GemstoneSocketingHelper.getGemstones(itemStack);

    for (Gemstone gem : gemstones) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), itemStack.getItem());
        if (modifier instanceof ModifierCustomCondition modifierCustomCondition) {
          ModifierCustomCondition newModifier = new ModifierCustomCondition(modifierCustomCondition.value,
              modifierCustomCondition.additionalValue, modifierCustomCondition.conditionType,
              modifierCustomCondition.itemType, modifierCustomCondition.gemstoneType);
          newModifier.setRarityType(gem.gemstoneRarityType());
          modifiers.add(newModifier);
        }
      }
    }

    return modifiers;
  }
}
