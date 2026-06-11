package name.modid.core.api.modifiers.config;

import java.util.ArrayList;

import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.LevelValues;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public sealed interface ModifierConfig extends ModifierConfigBase permits
    ModifierConfig.Attributes,
    ModifierConfig.Effects,
    ModifierConfig.Events,
    ModifierConfig.AmplifierConfig {

  sealed interface Attributes extends ModifierConfig permits AttributeConfig, MultiplyAttributeConfig {
  }

  sealed interface Events extends ModifierConfig
      permits HitMeleeConfig, HitProjectileConfig, OnMobDamageConfig, OnPlayerDamageConfig,
      BlockBreakConfig, BeforeBlockBreakConfig, PlayerConfig,
      AfterDeathConfig, OnFirstHitConfig, OnPotionBrewConfig, OnFishingConfig {
    LevelValues values();

    LevelValues additionalValues();

    EventType eventType();
  }

  sealed interface Effects extends ModifierConfig
      permits HitEffectMeleeConfig, HitEffectProjectileConfig, AreaEffectConfig {
  }

  record AttributeConfig(
      LevelValues values,
      Operation operation,
      RegistryEntry<EntityAttribute> attribute) implements Attributes {
  }

  record MultiplyAttributeConfig(
      ArrayList<AttributeConfig> instances) implements Attributes {
  }

  record HitEffectMeleeConfig(
      LevelValues chance,
      RegistryEntry<StatusEffect> effect,
      int duration,
      int amplifier,
      int maxStacks,
      boolean stacking) implements Effects {
  }

  record HitEffectProjectileConfig(
      LevelValues chance,
      RegistryEntry<StatusEffect> effect,
      int duration,
      int amplifier,
      int maxStacks,
      boolean stacking) implements Effects {
  }

  record AreaEffectConfig(
      LevelValues radiusLevels,
      Integer amplifier,
      Integer duration,
      Boolean notMe,
      Boolean onlyPlayers,
      RegistryEntry<StatusEffect> effect) implements Effects {
  }

  record HitMeleeConfig(
      LevelValues values,
      LevelValues additionalValues,
      EventType eventType) implements Events {
  }

  record HitProjectileConfig(
      LevelValues values,
      LevelValues additionalValues,
      EventType eventType) implements Events {
  }

  record OnMobDamageConfig(
      LevelValues values,
      LevelValues additionalValues,
      EventType eventType) implements Events {
  }

  record OnPlayerDamageConfig(
      LevelValues values,
      LevelValues additionalValues,
      EventType eventType) implements Events {
  }

  record BlockBreakConfig(
      LevelValues values,
      LevelValues additionalValues,
      EventType eventType) implements Events {
  }

  record BeforeBlockBreakConfig(
      LevelValues values,
      LevelValues additionalValues,
      EventType eventType) implements Events {
  }

  record PlayerConfig(
      LevelValues values,
      LevelValues additionalValues,
      EventType eventType) implements Events {
  }

  record AfterDeathConfig(
      LevelValues values,
      LevelValues additionalValues,
      EventType eventType) implements Events {
  }

  record OnFirstHitConfig(
      LevelValues values,
      LevelValues additionalValues,
      EventType eventType) implements Events {
  }

  record OnPotionBrewConfig(
      LevelValues values,
      LevelValues additionalValues,
      EventType eventType) implements Events {
  }

  record OnFishingConfig(
      LevelValues values,
      LevelValues additionalValues,
      EventType eventType) implements Events {
  }

  record AmplifierConfig(
      LevelValues values) implements ModifierConfig {
  }
}
