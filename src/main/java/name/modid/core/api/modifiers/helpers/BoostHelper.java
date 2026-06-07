package name.modid.core.api.modifiers.helpers;

import java.util.ArrayList;

import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.AreaEffectConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.AttributeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.BeforeBlockBreakConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.BlockBreakConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectMeleeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitMeleeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.MultiplyAttributeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnFishingConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnFirstHitConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnMobDamageConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnPlayerDamageConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnPotionBrewConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.PlayerConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.BoosterConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.AfterDeathConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.LevelValues;
import name.modid.core.api.modifiers.types.GemstoneType;
import net.minecraft.item.Item;

public final class BoostHelper {
  private BoostHelper() {
  }

  public static GemstoneModifier applyBoost(
      GemstoneComponent[] gemstones,
      int slotIndex,
      Item item,
      GemstoneModifier modifier) {
    double multiplier = getSlotMultiplier(gemstones, slotIndex, item);
    if (multiplier == 1.0) {
      return modifier;
    }

    ModifierConfig boostedConfig = scaleConfig(modifier.getConfig(), multiplier);
    return new GemstoneModifier(
        modifier.getGemstoneType(),
        modifier.getRarityType(),
        modifier.getItemCategory(),
        boostedConfig);
  }

  public static boolean isBooster(GemstoneModifier modifier) {
    return modifier.getConfig() instanceof BoosterConfig;
  }

  private static double getSlotMultiplier(GemstoneComponent[] gemstones, int slotIndex, Item item) {
    if (slotIndex <= 0 || slotIndex >= gemstones.length || !isBoosterGemstone(gemstones[slotIndex - 1])) {
      return 1.0;
    }

    return 1.0 + getEffectiveBoost(gemstones, slotIndex - 1, item);
  }

  private static double getEffectiveBoost(GemstoneComponent[] gemstones, int slotIndex, Item item) {
    GemstoneModifier modifier = getBoosterModifier(gemstones[slotIndex], item);
    if (!(modifier != null && modifier.getConfig() instanceof BoosterConfig boosterConfig)) {
      return 0.0;
    }

    double boost = boosterConfig.values().get(modifier.getRarityType());
    if (slotIndex > 0 && isBoosterGemstone(gemstones[slotIndex - 1])) {
      boost *= getSlotMultiplier(gemstones, slotIndex, item);
    }

    return boost;
  }

  private static GemstoneModifier getBoosterModifier(GemstoneComponent gemstone, Item item) {
    if (!isBoosterGemstone(gemstone)) {
      return null;
    }

    return ModifierHelper.getGemstoneModifierForItem(
        gemstone.gemstoneType(),
        gemstone.gemstoneQualityType(),
        item);
  }

  private static boolean isBoosterGemstone(GemstoneComponent gemstone) {
    return gemstone != null && gemstone.gemstoneType() == GemstoneType.POLYCHROME_CRYSTAL;
  }

  private static ModifierConfig scaleConfig(ModifierConfig config, double multiplier) {
    if (config instanceof AttributeConfig attr) {
      return new AttributeConfig(scale(attr.values(), multiplier), attr.operation(), attr.attribute());
    }

    if (config instanceof MultiplyAttributeConfig multi) {
      ArrayList<AttributeConfig> instances = new ArrayList<>();
      for (AttributeConfig attr : multi.instances()) {
        instances.add(new AttributeConfig(scale(attr.values(), multiplier), attr.operation(), attr.attribute()));
      }
      return new MultiplyAttributeConfig(instances);
    }

    if (config instanceof HitEffectMeleeConfig melee) {
      return new HitEffectMeleeConfig(scale(melee.chance(), multiplier), melee.effect(), melee.duration(),
          melee.amplifier(), melee.maxStacks(), melee.stacking());
    }

    if (config instanceof HitEffectProjectileConfig projectile) {
      return new HitEffectProjectileConfig(scale(projectile.chance(), multiplier), projectile.effect(),
          projectile.duration(), projectile.amplifier(), projectile.maxStacks(), projectile.stacking());
    }

    if (config instanceof AreaEffectConfig area) {
      return new AreaEffectConfig(scale(area.radiusLevels(), multiplier), area.amplifier(), area.duration(),
          area.notMe(), area.onlyPlayers(), area.effect());
    }

    if (config instanceof HitMeleeConfig melee) {
      LevelValues additionalValues = melee.eventType() == EventType.ON_HIT_EXP_ADDITIONAL_DAMAGE
          ? scaleDivisor(melee.additionalValues(), multiplier)
          : scale(melee.additionalValues(), multiplier);
      return new HitMeleeConfig(scale(melee.values(), multiplier), additionalValues,
          melee.eventType());
    }

    if (config instanceof HitProjectileConfig projectile) {
      LevelValues additionalValues = projectile.eventType() == EventType.ON_HIT_EXP_ADDITIONAL_DAMAGE
          ? scaleDivisor(projectile.additionalValues(), multiplier)
          : scale(projectile.additionalValues(), multiplier);
      return new HitProjectileConfig(scale(projectile.values(), multiplier),
          additionalValues, projectile.eventType());
    }

    if (config instanceof OnMobDamageConfig mobDamage) {
      return new OnMobDamageConfig(scale(mobDamage.values(), multiplier),
          scale(mobDamage.additionalValues(), multiplier), mobDamage.eventType());
    }

    if (config instanceof OnPlayerDamageConfig playerDamage) {
      return new OnPlayerDamageConfig(scale(playerDamage.values(), multiplier),
          scale(playerDamage.additionalValues(), multiplier), playerDamage.eventType());
    }

    if (config instanceof BlockBreakConfig blockBreak) {
      return new BlockBreakConfig(scale(blockBreak.values(), multiplier),
          scale(blockBreak.additionalValues(), multiplier), blockBreak.eventType());
    }

    if (config instanceof BeforeBlockBreakConfig beforeBlockBreak) {
      return new BeforeBlockBreakConfig(scale(beforeBlockBreak.values(), multiplier),
          scale(beforeBlockBreak.additionalValues(), multiplier), beforeBlockBreak.eventType());
    }

    if (config instanceof PlayerConfig player) {
      return new PlayerConfig(scale(player.values(), multiplier), scale(player.additionalValues(), multiplier),
          player.eventType());
    }

    if (config instanceof AfterDeathConfig afterDeath) {
      return new AfterDeathConfig(scale(afterDeath.values(), multiplier),
          scale(afterDeath.additionalValues(), multiplier), afterDeath.eventType());
    }

    if (config instanceof OnFirstHitConfig firstHit) {
      return new OnFirstHitConfig(scale(firstHit.values(), multiplier),
          scale(firstHit.additionalValues(), multiplier), firstHit.eventType());
    }

    if (config instanceof OnPotionBrewConfig potionBrew) {
      return new OnPotionBrewConfig(scale(potionBrew.values(), multiplier),
          scale(potionBrew.additionalValues(), multiplier), potionBrew.eventType());
    }

    if (config instanceof OnFishingConfig fishing) {
      return new OnFishingConfig(scale(fishing.values(), multiplier),
          scale(fishing.additionalValues(), multiplier), fishing.eventType());
    }

    if (config instanceof BoosterConfig booster) {
      return new BoosterConfig(scale(booster.values(), multiplier));
    }

    return config;
  }

  private static LevelValues scale(LevelValues values, double multiplier) {
    return values == null ? null : values.scaled(multiplier);
  }

  private static LevelValues scaleDivisor(LevelValues values, double multiplier) {
    if (values == null) {
      return null;
    }

    ArrayList<Double> scaledValues = new ArrayList<>();
    values.stream().forEach(value -> scaledValues.add(Math.max(1.0, value / multiplier)));
    return new LevelValues(scaledValues);
  }
}
