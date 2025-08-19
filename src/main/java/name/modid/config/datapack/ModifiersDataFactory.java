package name.modid.config.datapack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import name.modid.Gemstones;
import name.modid.config.datapack.ModifiersConfig.AttributeConfig;
import name.modid.config.datapack.ModifiersConfig.MultiplyAttributeConfig;
import name.modid.config.datapack.ModifiersConfig.OnBlockBreakConfig;
import name.modid.config.datapack.ModifiersConfig.OnHitConfig;
import name.modid.config.datapack.ModifiersConfig.OnHitEffectConfig;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierItemCaregory;
import name.modid.helpers.modifiers.modifierTypes.EventType;
import name.modid.helpers.modifiers.modifierTypes.ModifierAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierMultiplyAttribute;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHit;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffect;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnHitEffectProjectile;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public class ModifiersDataFactory {
  private static final Logger LOGGER = Gemstones.LOGGER;

  public static Map<ModifierItemCaregory, GemstoneModifier> createModifiers(
      GemstoneType gemstoneType) {
    LOGGER.info(
        "[ModifiersConfig] Attempting to create modifiers for gemstone type: {}",
        gemstoneType);
    Map<ModifierItemCaregory, GemstoneModifier> modifiers = new HashMap<>();
    ModifiersConfig config = ModifiersDataLoader.getLoadedConfigs().get(gemstoneType);

    if (config == null) {
      LOGGER.warn(
          "[ModifiersConfig] No datapack config found for gemstone type: {}. Returning empty modifiers.",
          gemstoneType);
      return modifiers;
    }

    if (config.modifiers == null || config.modifiers.isEmpty()) {
      LOGGER.warn(
          "[ModifiersConfig] No 'modifiers' defined for gemstone type: {} in datapack config. Returning empty modifiers.",
          gemstoneType);
      return modifiers;
    }

    LOGGER.info(
        "[ModifiersConfig] Processing {} modifier categories for gemstone type: {}",
        config.modifiers.size(),
        gemstoneType);

    config.modifiers.forEach((category, entry) -> {
      GemstoneModifier modifierInstance = null;
      if (entry == null) {
        LOGGER.warn(
            "[ModifiersConfig] Invalid modifier entry (null) for category {} in gemstone {}. Skipping.",
            category,
            gemstoneType);
        return;
      }

      switch (entry.type) {
        case ON_HIT_EFFECT:
          if (!(entry instanceof OnHitEffectConfig onHitConfig)) {
            LOGGER.warn(
                "[ModifiersConfig] Expected OnHitEffectConfig for type {} but got {}. Skipping.",
                entry.type,
                entry.getClass().getSimpleName());
            return;
          }

          StatusEffect statusEffect = Registries.STATUS_EFFECT.get(onHitConfig.effectId);

          if (statusEffect == null) {
            LOGGER.warn(
                "Failed to retrieve StatusEffect for ID '{}' in category {} for gemstone {}. Skipping this modifier.",
                onHitConfig.effectId,
                category,
                gemstoneType);
          } else {
            RegistryEntry<StatusEffect> effectEntry = Registries.STATUS_EFFECT
                .getEntry(onHitConfig.effectId)
                .orElse(null);

            if (effectEntry == null) {
              LOGGER.warn(
                  "Failed to retrieve StatusEffect RegistryEntry for ID '{}' in category {} for gemstone {}. Skipping this modifier.",
                  onHitConfig.effectId,
                  category,
                  gemstoneType);
              return;
            }

            if (category != ModifierItemCaregory.RANGED) {
              modifierInstance = new ModifierOnHitEffect(
                  new ArrayList<>(onHitConfig.chanceLevels),
                  onHitConfig.duration,
                  onHitConfig.amplifier,
                  category,
                  effectEntry,
                  onHitConfig.isStacking,
                  onHitConfig.maxStackCount,
                  gemstoneType);
            } else {
              modifierInstance = new ModifierOnHitEffectProjectile(
                  new ArrayList<>(onHitConfig.chanceLevels),
                  onHitConfig.duration,
                  onHitConfig.amplifier,
                  category,
                  effectEntry,
                  onHitConfig.isStacking,
                  onHitConfig.maxStackCount,
                  gemstoneType);
            }
          }

          if (modifierInstance != null) {
            LOGGER.debug(
                "[ModifiersConfig] Created {} modifier for category {} for gemstone {}.",
                modifierInstance.getClass().getSimpleName(),
                category,
                gemstoneType);
            modifiers.put(category, modifierInstance);
          }
          break;
        case ON_BLOCK_BREAK: {
          if (!(entry instanceof OnBlockBreakConfig onBlockBreakConfig)) {
            LOGGER.warn(
                "[ModifiersConfig] Expected OnBlockBreakConfig for type {} but got {}. Skipping.",
                entry.type,
                entry.getClass().getSimpleName());
            return;
          }

          EventType eventType = onBlockBreakConfig.eventType;

          if (eventType == null) {
            LOGGER.warn(
                "Failed to retrieve EventType for ID '{}' in category {} for gemstone {}. Skipping this modifier.",
                onBlockBreakConfig.eventType,
                category,
                gemstoneType);
          } else {
            modifierInstance = new ModifierOnBlockBreak(new ArrayList<Double>(onBlockBreakConfig.chanceLevels),
                new ArrayList<Double>(onBlockBreakConfig.valueLevels), category, eventType, gemstoneType);
          }

          if (modifierInstance != null) {
            LOGGER.debug(
                "[ModifiersConfig] Created {} modifier for category {} for gemstone {}.",
                modifierInstance.getClass().getSimpleName(),
                category,
                gemstoneType);
            modifiers.put(category, modifierInstance);
          }
          break;
        }
        case MULTIPLY_ATTRIBUTE: {
          if (!(entry instanceof MultiplyAttributeConfig multiplyAttributeConfig)) {
            LOGGER.warn(
                "[ModifiersConfig] Expected MultiplyAttributeConfig for type {} but got {}. Skipping.",
                entry.type,
                entry.getClass().getSimpleName());
            return;
          }

          ArrayList<ModifierAttribute> instances = new ArrayList<>();

          for (AttributeConfig attributeInstance : multiplyAttributeConfig.attributes) {
            EntityAttribute attribute = Registries.ATTRIBUTE.get(attributeInstance.attributeId);

            if (attribute == null) {
              LOGGER.warn(
                  "Failed to retrieve EntityAttribute for ID '{}' in category {} for gemstone {}. Skipping this modifier.",
                  attributeInstance.attributeId,
                  category,
                  gemstoneType);
            } else {
              RegistryEntry<EntityAttribute> attributeEntry = Registries.ATTRIBUTE
                  .getEntry(attributeInstance.attributeId)
                  .orElse(null);

              if (attributeEntry == null) {
                LOGGER.warn(
                    "Failed to retrieve EntityAttribute RegistryEntry for ID '{}' in category {} for gemstone {}. Skipping this modifier.",
                    attributeInstance.attributeId,
                    category,
                    gemstoneType);
                return;
              }

              instances.add(new ModifierAttribute(attributeInstance.operation,
                  new ArrayList<Double>(attributeInstance.valueLevels), category, attributeEntry, gemstoneType));
            }
          }

          if (!instances.isEmpty()) {
            modifierInstance = new ModifierMultiplyAttribute(instances);
          }

          if (modifierInstance != null) {
            LOGGER.debug(
                "[ModifiersConfig] Created {} modifier for category {} for gemstone {}.",
                modifierInstance.getClass().getSimpleName(),
                category,
                gemstoneType);
            modifiers.put(category, modifierInstance);
          }
          break;
        }
        case ATTRIBUTE: {
          if (!(entry instanceof AttributeConfig attributeModifierConfig)) {
            LOGGER.warn(
                "[ModifiersConfig] Expected AttributeConfig for type {} but got {}. Skipping.",
                entry.type,
                entry.getClass().getSimpleName());
            return;
          }

          EntityAttribute attribute = Registries.ATTRIBUTE.get(attributeModifierConfig.attributeId);

          if (attribute == null) {
            LOGGER.warn(
                "Failed to retrieve EntityAttribute for ID '{}' in category {} for gemstone {}. Skipping this modifier.",
                attributeModifierConfig.attributeId,
                category,
                gemstoneType);
          } else {
            RegistryEntry<EntityAttribute> attributeEntry = Registries.ATTRIBUTE
                .getEntry(attributeModifierConfig.attributeId)
                .orElse(null);

            if (attributeEntry == null) {
              LOGGER.warn(
                  "Failed to retrieve EntityAttribute RegistryEntry for ID '{}' in category {} for gemstone {}. Skipping this modifier.",
                  attributeModifierConfig.attributeId,
                  category,
                  gemstoneType);
              return;
            }

            modifierInstance = new ModifierAttribute(attributeModifierConfig.operation,
                new ArrayList<Double>(attributeModifierConfig.valueLevels), category, attributeEntry, gemstoneType);
          }

          if (modifierInstance != null) {
            LOGGER.debug(
                "[ModifiersConfig] Created {} modifier for category {} for gemstone {}.",
                modifierInstance.getClass().getSimpleName(),
                category,
                gemstoneType);
            modifiers.put(category, modifierInstance);
          }
          break;
        }
        case ON_HIT: {
          if (!(entry instanceof OnHitConfig onHitConfig)) {
            LOGGER.warn(
                "[ModifiersConfig] Expected OnHitConfig for type {} but got {}. Skipping.",
                entry.type,
                entry.getClass().getSimpleName());
            return;
          }

          EventType eventType = onHitConfig.eventType;

          if (eventType == null) {
            LOGGER.warn(
                "Failed to retrieve EventType for ID '{}' in category {} for gemstone {}. Skipping this modifier.",
                onHitConfig.eventType,
                category,
                gemstoneType);
          } else {
            modifierInstance = new ModifierOnHit(new ArrayList<Double>(onHitConfig.chanceLevels), eventType, category,
                gemstoneType);
          }

          if (modifierInstance != null) {
            LOGGER.debug(
                "[ModifiersConfig] Created {} modifier for category {} for gemstone {}.",
                modifierInstance.getClass().getSimpleName(),
                category,
                gemstoneType);
            modifiers.put(category, modifierInstance);
          }
          break;
        }
        default: {
          LOGGER.warn(
              "[ModifiersConfig] Unhandled modifier type for category {} in gemstone {}. Class: {}. Skipping.",
              category,
              gemstoneType,
              entry.getClass().getSimpleName());
          break;
        }
      }
    });

    LOGGER.info(
        "[ModifiersConfig] Finished creating {} modifiers for gemstone type: {}. Final map size: {}",
        modifiers.size(),
        gemstoneType,
        modifiers.size());
    return modifiers;
  }
}