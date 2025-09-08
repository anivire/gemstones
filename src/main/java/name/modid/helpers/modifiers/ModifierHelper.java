package name.modid.helpers.modifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import name.modid.config.data.modifiers.ModifiersRegistry;
import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneSocketingHelper;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.modifiers.category.ModifierAreaEffect;
import name.modid.helpers.modifiers.category.ModifierAttribute;
import name.modid.helpers.modifiers.category.ModifierCustomCondition;
import name.modid.helpers.modifiers.category.ModifierMultiplyAttribute;
import name.modid.helpers.modifiers.category.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.category.ModifierOnDamage;
import name.modid.helpers.modifiers.category.ModifierOnFirstHitMelee;
import name.modid.helpers.modifiers.category.ModifierOnHitEffectMelee;
import name.modid.helpers.modifiers.category.ModifierOnHitEffectProjectile;
import name.modid.helpers.modifiers.category.ModifierOnHitMelee;
import name.modid.helpers.modifiers.category.ModifierOnHitProjectile;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.instance.ModifierData;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
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
import net.minecraft.server.network.ServerPlayerEntity;

public class ModifierHelper {
  public static Map<ModifierItemCategory, Map<GemstoneRarity, GemstoneModifier>> getGemstoneModifiers(
      GemstoneType gemstoneType, Item item) {
    if (gemstoneType == GemstoneType.EMPTY || gemstoneType == GemstoneType.LOCKED) {
      return null;
    }

    ModifierData modifiersData = ModifierRegistration.MODIFIER_REGISTRY().get(gemstoneType);
    return modifiersData.getModifiers();
  }

  public static GemstoneModifier getGemstoneModifierForItem(
      GemstoneType gemstoneType, GemstoneRarity gemstoneRarityType, Item item) {
    if (gemstoneType == GemstoneType.EMPTY || gemstoneType == GemstoneType.LOCKED) {
      return null;
    }

    Map<ModifierItemCategory, Map<GemstoneRarity, GemstoneModifier>> modifiers = ModifiersRegistry
        .getModifiersForGemstone(gemstoneType);

    ModifierItemCategory category = getModifieritemSlot(item);
    Map<GemstoneRarity, GemstoneModifier> rarityMap = modifiers.get(category);

    if (rarityMap == null)
      return null;
    return rarityMap.get(gemstoneRarityType);
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

  public static <R> List<R> collectPlayerArmorValues(
      ServerPlayerEntity player,
      Function<ItemStack, List<R>> callback) {
    return Stream.of(
        player.getEquippedStack(EquipmentSlot.HEAD),
        player.getEquippedStack(EquipmentSlot.CHEST),
        player.getEquippedStack(EquipmentSlot.LEGS),
        player.getEquippedStack(EquipmentSlot.FEET))
        .filter(stack -> !stack.isEmpty())
        .map(callback)
        .flatMap(List::stream)
        .toList();
  }

  public static ArrayList<ModifierAttribute> getAttributeModifiers(ItemStack itemStack) {
    ArrayList<ModifierAttribute> modifiers = new ArrayList<>();
    for (Gemstone gem : GemstoneSocketingHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), gem.gemstoneRarityType(),
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
    for (Gemstone gem : GemstoneSocketingHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), gem.gemstoneRarityType(),
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
    for (Gemstone gem : GemstoneSocketingHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), gem.gemstoneRarityType(),
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
    for (Gemstone gem : GemstoneSocketingHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), gem.gemstoneRarityType(),
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
    for (Gemstone gem : GemstoneSocketingHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), gem.gemstoneRarityType(),
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
    for (Gemstone gem : GemstoneSocketingHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), gem.gemstoneRarityType(),
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
    for (Gemstone gem : GemstoneSocketingHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), gem.gemstoneRarityType(),
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
    for (Gemstone gem : GemstoneSocketingHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), gem.gemstoneRarityType(),
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
    for (Gemstone gem : GemstoneSocketingHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), gem.gemstoneRarityType(),
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
    for (Gemstone gem : GemstoneSocketingHelper.getGemstones(itemStack)) {
      if (gem.gemstoneType() != null && gem.gemstoneType() != GemstoneType.LOCKED) {
        GemstoneModifier modifier = getGemstoneModifierForItem(gem.gemstoneType(), gem.gemstoneRarityType(),
            itemStack.getItem());
        if (modifier instanceof ModifierCustomCondition m) {
          modifiers.add(m);
        }
      }
    }
    return modifiers;
  }
}