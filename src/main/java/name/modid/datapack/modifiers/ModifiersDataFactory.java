package name.modid.datapack.modifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.LevelValues;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public class ModifiersDataFactory {
  private static final Logger LOGGER = Gemstones.LOGGER;

  public static Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> createModifiers(
      GemstoneType gemstoneType) {

    LOGGER.info("[ModifiersConfig] Creating modifiers for gemstone type: {}", gemstoneType);
    Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> modifiers = new HashMap<>();
    ModifiersRawConfig config = ModifiersDataLoader.getLoadedConfigs().get(gemstoneType);

    if (config == null
        || config.modifiers == null
        || config.modifiers.isEmpty()) {
      LOGGER.warn(
          "[ModifiersConfig] No datapack config found or empty for gemstone type: {}. Returning empty modifiers.",
          gemstoneType);
      return modifiers;
    }

    config.modifiers.forEach((category, entry) -> {
      if (entry == null) {
        LOGGER.warn(
            "[ModifiersConfig] Null modifier entry for category {} in gemstone {}. Skipping.", category,
            gemstoneType);
        return;
      }

      Map<GemstoneQuality, GemstoneModifier> rarityMap = new HashMap<>();

      for (GemstoneQuality rarity : GemstoneQuality.values()) {
        if (rarity == GemstoneQuality.NONE
            || rarity == GemstoneQuality.UNUSUAL) {
          continue;
        }

        GemstoneModifier modifierInstance = null;

        switch (entry.type) {
          case ATTRIBUTE -> {
            if (entry instanceof ModifiersRawConfig.AttributeConfig raw) {
              EntityAttribute attr = Registries.ATTRIBUTE.get(raw.attributeId);
              if (attr == null)
                continue;
              RegistryEntry<EntityAttribute> attrEntry = Registries.ATTRIBUTE.getEntry(raw.attributeId).orElse(null);
              if (attrEntry == null)
                continue;

              ModifierConfig.AttributeConfig cfg = new ModifierConfig.AttributeConfig(
                  new LevelValues(new ArrayList<>(raw.valueLevels)),
                  raw.operation,
                  attrEntry);
              modifierInstance = new GemstoneModifier(gemstoneType, rarity, category, cfg);
            }
          }

          case MULTIPLY_ATTRIBUTE -> {
            if (entry instanceof ModifiersRawConfig.MultiplyAttributeConfig rawMulti) {
              var list = new ArrayList<ModifierConfig.AttributeConfig>();
              for (var sub : rawMulti.attributes) {
                EntityAttribute attr = Registries.ATTRIBUTE.get(sub.attributeId);
                if (attr == null)
                  continue;
                RegistryEntry<EntityAttribute> attrEntry = Registries.ATTRIBUTE.getEntry(sub.attributeId).orElse(null);
                if (attrEntry == null)
                  continue;

                list.add(new ModifierConfig.AttributeConfig(
                    new LevelValues(new ArrayList<>(sub.valueLevels)),
                    sub.operation,
                    attrEntry));
              }
              if (!list.isEmpty()) {
                ModifierConfig.MultiplyAttributeConfig cfg = new ModifierConfig.MultiplyAttributeConfig(list);
                modifierInstance = new GemstoneModifier(gemstoneType, rarity, category, cfg);
              }
            }
          }

          case ON_HIT -> {
            if (entry instanceof ModifiersRawConfig.OnHitConfig raw) {
              EventType ev = raw.eventType;
              if (ev == null)
                continue;

              ModifierConfig.HitMeleeConfig cfg = new ModifierConfig.HitMeleeConfig(
                  new LevelValues(new ArrayList<>(raw.chanceLevels)),
                  ev);
              modifierInstance = new GemstoneModifier(gemstoneType, rarity, category, cfg);
            }
          }

          case ON_HIT_EFFECT -> {
            if (entry instanceof ModifiersRawConfig.OnHitEffectConfig raw) {
              StatusEffect se = Registries.STATUS_EFFECT.get(raw.effectId);
              if (se == null)
                continue;
              RegistryEntry<StatusEffect> seEntry = Registries.STATUS_EFFECT.getEntry(raw.effectId).orElse(null);
              if (seEntry == null)
                continue;

              ModifierConfig.HitEffectMeleeConfig cfg = new ModifierConfig.HitEffectMeleeConfig(
                  new LevelValues(new ArrayList<>(raw.chanceLevels)),
                  seEntry,
                  raw.duration,
                  raw.amplifier,
                  raw.maxStackCount != null ? raw.maxStackCount : 0,
                  raw.isStacking != null && raw.isStacking);
              modifierInstance = new GemstoneModifier(gemstoneType, rarity, category, cfg);
            }
          }

          case AREA_EFFECT -> {
            if (entry instanceof ModifiersRawConfig.AreaEffectConfig raw) {
              StatusEffect se = Registries.STATUS_EFFECT.get(raw.effectId);
              if (se == null)
                continue;
              RegistryEntry<StatusEffect> seEntry = Registries.STATUS_EFFECT.getEntry(raw.effectId).orElse(null);
              if (seEntry == null)
                continue;

              ModifierConfig.AreaEffectConfig cfg = new ModifierConfig.AreaEffectConfig(
                  new LevelValues(new ArrayList<>(raw.radiusLevels)),
                  raw.amplifier,
                  raw.duration,
                  raw.notMe,
                  raw.onlyPlayers,
                  seEntry);
              modifierInstance = new GemstoneModifier(gemstoneType, rarity, category, cfg);
            }
          }

          case ON_BLOCK_BREAK -> {
            if (entry instanceof ModifiersRawConfig.OnBlockBreakConfig raw) {
              if (raw.eventType == null)
                continue;

              ModifierConfig.BlockBreakConfig cfg = new ModifierConfig.BlockBreakConfig(
                  new LevelValues(new ArrayList<>(raw.chanceLevels)),
                  new LevelValues(new ArrayList<>(raw.valueLevels)),
                  raw.eventType);
              modifierInstance = new GemstoneModifier(gemstoneType, rarity, category, cfg);
            }
          }

          case ON_FIRST_HIT -> {
            if (entry instanceof ModifiersRawConfig.OnFirstHitConfig raw) {
              if (raw.eventType == null)
                continue;

              ModifierConfig.OnFirstHitConfig cfg = new ModifierConfig.OnFirstHitConfig(
                  new LevelValues(new ArrayList<>(raw.valueLevels)),
                  raw.eventType);
              modifierInstance = new GemstoneModifier(gemstoneType, rarity, category, cfg);
            }
          }

          case CUSTOM_CONDITION -> {
            if (entry instanceof ModifiersRawConfig.CustomConditionConfig raw) {
              if (raw.eventType == null)
                continue;

              ModifierConfig.CustomConditionConfig cfg = new ModifierConfig.CustomConditionConfig(
                  new LevelValues(new ArrayList<>(raw.valueLevels)),
                  new LevelValues(new ArrayList<>(raw.additionalValueLevels)),
                  raw.eventType);
              modifierInstance = new GemstoneModifier(gemstoneType, rarity, category, cfg);
            }
          }

          default -> {
            LOGGER.warn("[ModifiersConfig] Unhandled modifier type {} for gemstone {}.", entry.type, gemstoneType);
          }
        }

        if (modifierInstance != null) {
          rarityMap.put(rarity, modifierInstance);
        }
      }

      if (!rarityMap.isEmpty()) {
        modifiers.put(category, rarityMap);
      }
    });

    LOGGER.info("[ModifiersConfig] Finished creating {} modifier categories for gemstone type: {}.",
        modifiers.size(), gemstoneType);
    return modifiers;
  }
}