package name.modid.datapack.modifiers;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import name.modid.core.api.modifiers.config.ModifierCategoryType;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.AttributeConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.LevelValues;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

// TODO: cleanup and refactor
public class ModifiersConfigDeserializer implements JsonDeserializer<ModifierConfig> {
  @Override
  public ModifierConfig deserialize(
      JsonElement json,
      Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {

    JsonObject obj = json.getAsJsonObject();

    ModifierCategoryType type;
    try {
      type = parseModifierType(obj.get("type").getAsString());
    } catch (Exception e) {
      type = ModifierCategoryType.UNDEFINED;
    }

    return switch (type) {
      case ATTRIBUTE -> {
        Identifier attributeId = context.deserialize(obj.get("attribute_id"), Identifier.class);
        RegistryKey<EntityAttribute> attrKey = RegistryKey.of(RegistryKeys.ATTRIBUTE, attributeId);
        RegistryEntry<EntityAttribute> attrEntry = Registries.ATTRIBUTE.getEntry(attrKey)
            .orElseThrow(() -> new JsonParseException("Unknown attribute: " + attributeId));
        LevelValues values = context.deserialize(obj.get("value_levels"), LevelValues.class);
        Operation operation = Operation.valueOf(obj.get("operation").getAsString());
        yield new ModifierConfig.AttributeConfig(values, operation, attrEntry);
      }

      case MULTIPLY_ATTRIBUTE -> {
        var list = new ArrayList<ModifierConfig.AttributeConfig>();
        for (var elem : obj.getAsJsonArray("instances")) {
          list.add(deserializeAttribute(elem.getAsJsonObject(), context));
        }
        yield new ModifierConfig.MultiplyAttributeConfig(list);
      }

      case ON_HIT_MELEE -> {
        LevelValues chance = context.deserialize(obj.get("chance_levels"), LevelValues.class);
        LevelValues additional_values = context.deserialize(obj.get("additional_value_levels"), LevelValues.class);
        EventType event = deserializeEvent(obj);
        yield new ModifierConfig.HitMeleeConfig(chance, additional_values, event);
      }

      case ON_HIT_PROJECTILE -> {
        LevelValues chance = context.deserialize(obj.get("chance_levels"), LevelValues.class);
        LevelValues additional_values = context.deserialize(obj.get("additional_value_levels"), LevelValues.class);
        EventType event = deserializeEvent(obj);
        yield new ModifierConfig.HitProjectileConfig(chance, additional_values, event);
      }

      case ON_HIT_EFFECT_MELEE -> {
        LevelValues chance = context.deserialize(obj.get("chance_levels"), LevelValues.class);
        Identifier effectId = context.deserialize(obj.get("effect_id"), Identifier.class);
        RegistryKey<StatusEffect> effectKey = RegistryKey.of(RegistryKeys.STATUS_EFFECT, effectId);
        RegistryEntry<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(effectKey)
            .orElseThrow(() -> new JsonParseException("Unknown effect: " + effectId));
        int duration = obj.get("duration").getAsInt();
        int amplifier = obj.get("amplifier").getAsInt();
        int maxStacks = obj.has("max_stack_count") ? obj.get("max_stack_count").getAsInt() : 0;
        boolean stacking = obj.has("is_stacking") && obj.get("is_stacking").getAsBoolean();
        yield new ModifierConfig.HitEffectMeleeConfig(
            chance, effectEntry, duration, amplifier, maxStacks, stacking);
      }

      case ON_HIT_EFFECT_PROJECTILE -> {
        LevelValues chance = context.deserialize(obj.get("chance_levels"), LevelValues.class);
        Identifier effectId = context.deserialize(obj.get("effect_id"), Identifier.class);
        RegistryKey<StatusEffect> effectKey = RegistryKey.of(RegistryKeys.STATUS_EFFECT, effectId);
        RegistryEntry<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(effectKey)
            .orElseThrow(() -> new JsonParseException("Unknown effect: " + effectId));
        int duration = obj.get("duration").getAsInt();
        int amplifier = obj.get("amplifier").getAsInt();
        int maxStacks = obj.has("max_stack_count") ? obj.get("max_stack_count").getAsInt() : 0;
        boolean stacking = obj.has("is_stacking") && obj.get("is_stacking").getAsBoolean();
        yield new ModifierConfig.HitEffectProjectileConfig(
            chance, effectEntry, duration, amplifier, maxStacks, stacking);
      }

      case AREA_EFFECT -> {
        LevelValues radius = context.deserialize(obj.get("radius_levels"), LevelValues.class);
        Identifier effectId = context.deserialize(obj.get("effect_id"), Identifier.class);
        RegistryKey<StatusEffect> effectKey = RegistryKey.of(RegistryKeys.STATUS_EFFECT, effectId);
        RegistryEntry<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(effectKey)
            .orElseThrow(() -> new JsonParseException("Unknown effect: " + effectId));
        int duration = obj.get("duration").getAsInt();
        int amplifier = obj.get("amplifier").getAsInt();
        Boolean notMe = obj.has("not_me") && obj.get("not_me").getAsBoolean();
        Boolean onlyPlayers = obj.has("only_players") && obj.get("only_players").getAsBoolean();
        yield new ModifierConfig.AreaEffectConfig(radius, amplifier, duration, notMe, onlyPlayers, effectEntry);
      }

      case AMPLIFIER -> {
        LevelValues values = context.deserialize(obj.get("value_levels"), LevelValues.class);
        yield new ModifierConfig.AmplifierConfig(values);
      }

      case ON_BLOCK_BREAK -> {
        LevelValues chance = context.deserialize(obj.get("chance_levels"), LevelValues.class);
        LevelValues values = context.deserialize(obj.get("value_levels"), LevelValues.class);
        EventType event = deserializeEvent(obj);
        yield new ModifierConfig.BlockBreakConfig(chance, values, event);
      }

      case ON_BEFORE_BLOCK_BREAK -> {
        LevelValues chance = context.deserialize(obj.get("chance_levels"), LevelValues.class);
        LevelValues values = context.deserialize(obj.get("value_levels"), LevelValues.class);
        EventType event = deserializeEvent(obj);
        yield new ModifierConfig.BeforeBlockBreakConfig(chance, values, event);
      }

      case ON_FIRST_HIT -> {
        LevelValues values = context.deserialize(obj.get("value_levels"), LevelValues.class);
        LevelValues additional_values = context.deserialize(obj.get("additional_value_levels"), LevelValues.class);
        EventType event = deserializeEvent(obj);
        yield new ModifierConfig.OnFirstHitConfig(values, additional_values, event);
      }

      case ON_POTION_BREW -> {
        LevelValues values = context.deserialize(obj.get("value_levels"), LevelValues.class);
        LevelValues additional_values = context.deserialize(obj.get("additional_value_levels"), LevelValues.class);
        EventType event = deserializeEvent(obj);
        yield new ModifierConfig.OnPotionBrewConfig(values, additional_values, event);
      }

      case PLAYER -> {
        LevelValues values = context.deserialize(obj.get("value_levels"), LevelValues.class);
        LevelValues additional_values = context.deserialize(obj.get("additional_value_levels"), LevelValues.class);
        EventType event = deserializeEvent(obj);
        yield new ModifierConfig.PlayerConfig(values, additional_values, event);
      }

      case ON_MOB_DAMAGE -> {
        LevelValues values = context.deserialize(obj.get("value_levels"), LevelValues.class);
        LevelValues additional_values = context.deserialize(obj.get("additional_value_levels"), LevelValues.class);
        EventType event = deserializeEvent(obj);
        yield new ModifierConfig.OnMobDamageConfig(values, additional_values, event);
      }

      case ON_PLAYER_DAMAGE -> {
        LevelValues values = context.deserialize(obj.get("value_levels"), LevelValues.class);
        LevelValues additional_values = context.deserialize(obj.get("additional_value_levels"), LevelValues.class);
        EventType event = deserializeEvent(obj);
        yield new ModifierConfig.OnPlayerDamageConfig(values, additional_values, event);
      }

      case ON_DEATH -> {
        LevelValues values = context.deserialize(obj.get("value_levels"), LevelValues.class);
        LevelValues additional_values = context.deserialize(obj.get("additional_value_levels"), LevelValues.class);
        EventType event = deserializeEvent(obj);
        yield new ModifierConfig.AfterDeathConfig(values, additional_values, event);
      }

      case ON_FISHING -> {
        LevelValues values = context.deserialize(obj.get("value_levels"), LevelValues.class);
        LevelValues additional_values = context.deserialize(obj.get("additional_value_levels"), LevelValues.class);
        EventType event = deserializeEvent(obj);
        yield new ModifierConfig.OnFishingConfig(values, additional_values, event);
      }

      case UNDEFINED -> throw new JsonParseException("Unknown/unsupported modifier type: " + obj.get("type"));
      default -> throw new JsonParseException("Unknown/unsupported modifier type: " + obj.get("type"));
    };
  }

  private AttributeConfig deserializeAttribute(
      JsonObject obj,
      JsonDeserializationContext context) {
    Identifier attributeId = context.deserialize(obj.get("attribute_id"), Identifier.class);
    RegistryKey<EntityAttribute> attrKey = RegistryKey.of(RegistryKeys.ATTRIBUTE, attributeId);
    RegistryEntry<EntityAttribute> attrEntry = Registries.ATTRIBUTE.getEntry(attrKey)
        .orElseThrow(() -> new JsonParseException("Unknown attribute: " + attributeId));
    LevelValues values = context.deserialize(obj.get("value_levels"), LevelValues.class);
    Operation operation = Operation.valueOf(obj.get("operation").getAsString());

    return new ModifierConfig.AttributeConfig(values, operation, attrEntry);
  }

  private ModifierCategoryType parseModifierType(String name) {
    return switch (name.toUpperCase()) {
      case "BOOSTER" -> ModifierCategoryType.AMPLIFIER;
      default -> ModifierCategoryType.valueOf(name.toUpperCase());
    };
  }

  private EventType deserializeEvent(JsonObject obj) {
    if (!obj.has("event_type")) {
      throw new JsonParseException("Missing event_type");
    }

    try {
      return EventType.fromString(obj.get("event_type").getAsString());
    } catch (IllegalArgumentException ex) {
      throw new JsonParseException(ex.getMessage());
    }
  }
}
