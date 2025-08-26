package name.modid.config.datapack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import name.modid.Gemstones;
import name.modid.config.datapack.ModifiersConfig.AreaEffectConfig;
import name.modid.config.datapack.ModifiersConfig.AttributeConfig;
import name.modid.config.datapack.ModifiersConfig.CustomConditionConfig;
import name.modid.config.datapack.ModifiersConfig.MultiplyAttributeConfig;
import name.modid.config.datapack.ModifiersConfig.OnBlockBreakConfig;
import name.modid.config.datapack.ModifiersConfig.OnFirstHitConfig;
import name.modid.config.datapack.ModifiersConfig.OnHitConfig;
import name.modid.config.datapack.ModifiersConfig.OnHitEffectConfig;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.category.ModifierAreaEffect;
import name.modid.helpers.modifiers.category.ModifierAttribute;
import name.modid.helpers.modifiers.category.ModifierCustomCondition;
import name.modid.helpers.modifiers.category.ModifierMultiplyAttribute;
import name.modid.helpers.modifiers.category.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.category.ModifierOnFirstHit;
import name.modid.helpers.modifiers.category.ModifierOnHit;
import name.modid.helpers.modifiers.category.ModifierOnHitEffect;
import name.modid.helpers.modifiers.category.ModifierOnHitEffectProjectile;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.type.ConditionType;
import name.modid.helpers.modifiers.type.EventType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public class ModifiersDataFactory {
  private static final Logger LOGGER = Gemstones.LOGGER;

  public static Map<ModifierItemCategory, GemstoneModifier> createModifiers(GemstoneType gemstoneType) {
    LOGGER.info("[ModifiersConfig] Attempting to create modifiers for gemstone type: {}", gemstoneType);
    Map<ModifierItemCategory, GemstoneModifier> modifiers = new HashMap<>();
    ModifiersConfig config = ModifiersDataLoader.getLoadedConfigs().get(gemstoneType);

    if (config == null || config.modifiers == null || config.modifiers.isEmpty()) {
      LOGGER.warn(
          "[ModifiersConfig] No datapack config found or empty for gemstone type: {}. Returning empty modifiers.",
          gemstoneType);
      return modifiers;
    }

    config.modifiers.forEach((category, entry) -> {
      GemstoneModifier modifierInstance = null;
      if (entry == null) {
        LOGGER.warn("[ModifiersConfig] Null modifier entry for category {} in gemstone {}. Skipping.", category,
            gemstoneType);
        return;
      }

      switch (entry.type) {
        case ON_HIT_EFFECT -> {
          if (entry instanceof OnHitEffectConfig onHitConfig) {
            StatusEffect statusEffect = Registries.STATUS_EFFECT.get(onHitConfig.effectId);
            if (statusEffect == null) {
              LOGGER.warn("Invalid StatusEffect '{}' for gemstone {}. Skipping.", onHitConfig.effectId, gemstoneType);
              return;
            }
            RegistryEntry<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(onHitConfig.effectId)
                .orElse(null);

            if (effectEntry == null) {
              return;
            }

            if (category != ModifierItemCategory.RANGED) {
              modifierInstance = new ModifierOnHitEffect(
                  gemstoneType,
                  null,
                  category,
                  new ArrayList<>(onHitConfig.chanceLevels),
                  effectEntry,
                  onHitConfig.duration,
                  onHitConfig.amplifier,
                  onHitConfig.maxStackCount,
                  onHitConfig.isStacking);
            } else {
              modifierInstance = new ModifierOnHitEffectProjectile(
                  gemstoneType,
                  null,
                  category,
                  new ArrayList<>(onHitConfig.chanceLevels),
                  effectEntry,
                  onHitConfig.duration,
                  onHitConfig.amplifier,
                  onHitConfig.maxStackCount,
                  onHitConfig.isStacking);
            }
          }
        }
        case AREA_EFFECT -> {
          if (entry instanceof AreaEffectConfig areaEffectConfig) {
            StatusEffect statusEffect = Registries.STATUS_EFFECT.get(areaEffectConfig.effectId);
            if (statusEffect == null) {
              return;
            }

            RegistryEntry<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(areaEffectConfig.effectId)
                .orElse(null);

            if (effectEntry == null) {
              return;
            }

            modifierInstance = new ModifierAreaEffect(
                gemstoneType,
                null,
                category,
                new ArrayList<>(areaEffectConfig.radiusLevels),
                areaEffectConfig.amplifier,
                areaEffectConfig.duration,
                areaEffectConfig.notMe,
                effectEntry);
          }
        }
        case ON_BLOCK_BREAK -> {
          if (entry instanceof OnBlockBreakConfig onBlockBreakConfig) {
            EventType eventType = onBlockBreakConfig.eventType;

            if (eventType == null) {
              return;
            }

            modifierInstance = new ModifierOnBlockBreak(
                gemstoneType,
                null,
                category,
                new ArrayList<>(onBlockBreakConfig.chanceLevels),
                new ArrayList<>(onBlockBreakConfig.valueLevels),
                eventType);
          }
        }
        case MULTIPLY_ATTRIBUTE -> {
          if (entry instanceof MultiplyAttributeConfig multiplyAttributeConfig) {
            ArrayList<ModifierAttribute> instances = new ArrayList<>();
            for (AttributeConfig attributeInstance : multiplyAttributeConfig.attributes) {
              EntityAttribute attribute = Registries.ATTRIBUTE.get(attributeInstance.attributeId);

              if (attribute == null) {
                continue;
              }

              RegistryEntry<EntityAttribute> attributeEntry = Registries.ATTRIBUTE
                  .getEntry(attributeInstance.attributeId).orElse(null);

              if (attributeEntry == null) {
                continue;
              }

              instances.add(new ModifierAttribute(
                  gemstoneType,
                  null,
                  category,
                  new ArrayList<>(attributeInstance.valueLevels),
                  attributeInstance.operation,
                  attributeEntry));
            }
            if (!instances.isEmpty()) {
              modifierInstance = new ModifierMultiplyAttribute(
                  gemstoneType,
                  null,
                  category,
                  instances);
            }
          }
        }
        case ATTRIBUTE -> {
          if (entry instanceof AttributeConfig attributeModifierConfig) {
            EntityAttribute attribute = Registries.ATTRIBUTE.get(attributeModifierConfig.attributeId);

            if (attribute == null) {
              return;
            }

            RegistryEntry<EntityAttribute> attributeEntry = Registries.ATTRIBUTE
                .getEntry(attributeModifierConfig.attributeId).orElse(null);

            if (attributeEntry == null) {
              return;
            }

            modifierInstance = new ModifierAttribute(
                gemstoneType,
                null,
                category,
                new ArrayList<>(attributeModifierConfig.valueLevels),
                attributeModifierConfig.operation,
                attributeEntry);
          }
        }
        case ON_HIT -> {
          if (entry instanceof OnHitConfig onHitConfig) {
            EventType eventType = onHitConfig.eventType;

            if (eventType == null) {
              return;
            }

            modifierInstance = new ModifierOnHit(
                gemstoneType,
                null,
                category,
                new ArrayList<>(onHitConfig.chanceLevels),
                eventType);
          }
        }
        case CUSTOM_CONDITION -> {
          if (entry instanceof CustomConditionConfig customConditionConfig) {
            ConditionType conditionType = customConditionConfig.conditionType;

            if (conditionType == null) {
              return;
            }

            modifierInstance = new ModifierCustomCondition(
                gemstoneType,
                null,
                category,
                new ArrayList<>(customConditionConfig.valueLevels),
                new ArrayList<>(customConditionConfig.additionalValueLevels),
                conditionType);
          }
        }
        case ON_FIRST_HIT -> {
          if (entry instanceof OnFirstHitConfig onFirstHitConfig) {
            EventType eventType = onFirstHitConfig.eventType;

            if (eventType == null) {
              return;
            }

            modifierInstance = new ModifierOnFirstHit(
                gemstoneType,
                null,
                category,
                new ArrayList<>(onFirstHitConfig.valueLevels),
                eventType);
          }
        }
        default -> {
          LOGGER.warn("[ModifiersConfig] Unhandled modifier type {} for gemstone {}.", entry.type, gemstoneType);
        }
      }

      if (modifierInstance != null) {
        LOGGER.debug("[ModifiersConfig] Created {} modifier for category {} for gemstone {}.",
            modifierInstance.getClass().getSimpleName(), category, gemstoneType);
        modifiers.put(category, modifierInstance);
      }
    });

    LOGGER.info("[ModifiersConfig] Finished creating {} modifiers for gemstone type: {}.", modifiers.size(),
        gemstoneType);

    return modifiers;
  }
}