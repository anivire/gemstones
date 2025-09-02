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
import name.modid.helpers.GemstoneRarity;
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
import name.modid.helpers.modifiers.type.EventType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public class ModifiersDataFactory {
  private static final Logger LOGGER = Gemstones.LOGGER;

  public static Map<ModifierItemCategory, Map<GemstoneRarity, GemstoneModifier>> createModifiers(
      GemstoneType gemstoneType) {
    LOGGER.info("[ModifiersConfig] Attempting to create modifiers for gemstone type: {}", gemstoneType);
    Map<ModifierItemCategory, Map<GemstoneRarity, GemstoneModifier>> modifiers = new HashMap<>();
    ModifiersConfig config = ModifiersDataLoader.getLoadedConfigs().get(gemstoneType);

    if (config == null || config.modifiers == null || config.modifiers.isEmpty()) {
      LOGGER.warn(
          "[ModifiersConfig] No datapack config found or empty for gemstone type: {}. Returning empty modifiers.",
          gemstoneType);
      return modifiers;
    }

    config.modifiers.forEach((category, entry) -> {
      if (entry == null) {
        LOGGER.warn("[ModifiersConfig] Null modifier entry for category {} in gemstone {}. Skipping.", category,
            gemstoneType);
        return;
      }

      Map<GemstoneRarity, GemstoneModifier> rarityMap = new HashMap<>();

      for (GemstoneRarity rarity : GemstoneRarity.values()) {
        if (rarity == GemstoneRarity.NONE || rarity == GemstoneRarity.UNUSUAL) {
          continue;
        }

        GemstoneModifier modifierInstance = null;

        switch (entry.type) {
          case ON_HIT_EFFECT -> {
            if (entry instanceof OnHitEffectConfig onHitConfig) {
              StatusEffect statusEffect = Registries.STATUS_EFFECT.get(onHitConfig.effectId);
              if (statusEffect == null) {
                LOGGER.warn("Invalid StatusEffect '{}' for gemstone {}. Skipping.", onHitConfig.effectId, gemstoneType);
                continue;
              }
              RegistryEntry<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(onHitConfig.effectId)
                  .orElse(null);
              if (effectEntry == null)
                continue;

              if (category != ModifierItemCategory.RANGED) {
                modifierInstance = new ModifierOnHitEffect(
                    gemstoneType,
                    rarity,
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
                    rarity,
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
              if (statusEffect == null)
                continue;
              RegistryEntry<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(areaEffectConfig.effectId)
                  .orElse(null);
              if (effectEntry == null)
                continue;

              modifierInstance = new ModifierAreaEffect(
                  gemstoneType,
                  rarity,
                  category,
                  new ArrayList<>(areaEffectConfig.radiusLevels),
                  areaEffectConfig.amplifier,
                  areaEffectConfig.duration,
                  areaEffectConfig.notMe,
                  areaEffectConfig.onlyPlayers,
                  effectEntry);
            }
          }
          case ON_BLOCK_BREAK -> {
            if (entry instanceof OnBlockBreakConfig onBlockBreakConfig) {
              EventType eventType = onBlockBreakConfig.eventType;
              if (eventType == null)
                continue;

              modifierInstance = new ModifierOnBlockBreak(
                  gemstoneType,
                  rarity,
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
                if (attribute == null)
                  continue;
                RegistryEntry<EntityAttribute> attributeEntry = Registries.ATTRIBUTE
                    .getEntry(attributeInstance.attributeId).orElse(null);
                if (attributeEntry == null)
                  continue;

                instances.add(new ModifierAttribute(
                    gemstoneType,
                    rarity,
                    category,
                    new ArrayList<>(attributeInstance.valueLevels),
                    attributeInstance.operation,
                    attributeEntry));
              }
              if (!instances.isEmpty()) {
                modifierInstance = new ModifierMultiplyAttribute(
                    gemstoneType,
                    rarity,
                    category,
                    instances);
              }
            }
          }
          case ATTRIBUTE -> {
            if (entry instanceof AttributeConfig attributeModifierConfig) {
              EntityAttribute attribute = Registries.ATTRIBUTE.get(attributeModifierConfig.attributeId);
              if (attribute == null)
                continue;
              RegistryEntry<EntityAttribute> attributeEntry = Registries.ATTRIBUTE
                  .getEntry(attributeModifierConfig.attributeId).orElse(null);
              if (attributeEntry == null)
                continue;

              modifierInstance = new ModifierAttribute(
                  gemstoneType,
                  rarity,
                  category,
                  new ArrayList<>(attributeModifierConfig.valueLevels),
                  attributeModifierConfig.operation,
                  attributeEntry);
            }
          }
          case ON_HIT -> {
            if (entry instanceof OnHitConfig onHitConfig) {
              EventType eventType = onHitConfig.eventType;
              if (eventType == null)
                continue;

              modifierInstance = new ModifierOnHit(
                  gemstoneType,
                  rarity,
                  category,
                  new ArrayList<>(onHitConfig.chanceLevels),
                  eventType);
            }
          }
          case CUSTOM_CONDITION -> {
            if (entry instanceof CustomConditionConfig customConditionConfig) {
              EventType eventType = customConditionConfig.eventType;
              if (eventType == null)
                continue;

              modifierInstance = new ModifierCustomCondition(
                  gemstoneType,
                  rarity,
                  category,
                  new ArrayList<>(customConditionConfig.valueLevels),
                  new ArrayList<>(customConditionConfig.additionalValueLevels),
                  eventType);
            }
          }
          case ON_FIRST_HIT -> {
            if (entry instanceof OnFirstHitConfig onFirstHitConfig) {
              EventType eventType = onFirstHitConfig.eventType;
              if (eventType == null)
                continue;

              modifierInstance = new ModifierOnFirstHit(
                  gemstoneType,
                  rarity,
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
          rarityMap.put(rarity, modifierInstance);
        }
      }

      if (!rarityMap.isEmpty()) {
        modifiers.put(category, rarityMap);
      }
    });

    LOGGER.info("[ModifiersConfig] Finished creating {} modifier categories for gemstone type: {}.", modifiers.size(),
        gemstoneType);
    return modifiers;
  }
}