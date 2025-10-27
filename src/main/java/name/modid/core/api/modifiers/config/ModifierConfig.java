package name.modid.core.api.modifiers.config;

import java.util.ArrayList;

import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.LevelValues;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public sealed interface ModifierConfig permits
    ModifierConfig.AttributeConfig,
    ModifierConfig.MultiplyAttributeConfig,
    ModifierConfig.HitMeleeConfig,
    ModifierConfig.HitProjectileConfig,
    ModifierConfig.HitEffectMeleeConfig,
    ModifierConfig.HitEffectProjectileConfig,
    ModifierConfig.AreaEffectConfig,
    ModifierConfig.OnDamageConfig,
    ModifierConfig.OnPotionBrewConfig,
    ModifierConfig.BlockBreakConfig,
    ModifierConfig.BeforeBlockBreakConfig,
    ModifierConfig.PlayerConfig,
    ModifierConfig.AfterDeathConfig,
    ModifierConfig.OnFishingConfig,
    ModifierConfig.OnFirstHitConfig {

  public record AttributeConfig(
      LevelValues values,
      Operation operation,
      RegistryEntry<EntityAttribute> attribute) implements ModifierConfig {
  }

  public record MultiplyAttributeConfig(
      ArrayList<AttributeConfig> instances) implements ModifierConfig {
  }

  public record HitMeleeConfig(
      LevelValues chance,
      LevelValues additionValues,
      EventType eventType) implements ModifierConfig {
  }

  public record HitProjectileConfig(
      LevelValues chance,
      LevelValues additionValues,
      EventType eventType) implements ModifierConfig {
  }

  public record HitEffectMeleeConfig(
      LevelValues chance,
      RegistryEntry<StatusEffect> effect,
      int duration,
      int amplifier,
      int maxStacks,
      boolean stacking) implements ModifierConfig {
  }

  public record HitEffectProjectileConfig(
      LevelValues chance,
      RegistryEntry<StatusEffect> effect,
      int duration,
      int amplifier,
      int maxStacks,
      boolean stacking) implements ModifierConfig {
  }

  public record AreaEffectConfig(
      LevelValues radiusLevels,
      Integer amplifier,
      Integer duration,
      Boolean notMe,
      Boolean onlyPlayers,
      RegistryEntry<StatusEffect> effect) implements ModifierConfig {
  }

  public record OnDamageConfig(
      LevelValues values,
      LevelValues additionalValues,
      EventType eventType) implements ModifierConfig {
  }

  public record BlockBreakConfig(
      LevelValues values,
      LevelValues additionValues,
      EventType eventType) implements ModifierConfig {
  }

  public record BeforeBlockBreakConfig(
      LevelValues values,
      LevelValues additionValues,
      EventType eventType) implements ModifierConfig {
  }

  public record PlayerConfig(
      LevelValues values,
      LevelValues additionValues,
      EventType eventType) implements ModifierConfig {
  }

  public record AfterDeathConfig(
      LevelValues values,
      LevelValues additionValues,
      EventType eventType) implements ModifierConfig {
  }

  public record OnFirstHitConfig(
      LevelValues values,
      EventType eventType) implements ModifierConfig {
  }

  public record OnPotionBrewConfig(
      LevelValues values,
      LevelValues additionValues,
      EventType eventType) implements ModifierConfig {
  }

  public record OnFishingConfig(
      LevelValues values,
      LevelValues additionValues,
      EventType eventType) implements ModifierConfig {
  }
}