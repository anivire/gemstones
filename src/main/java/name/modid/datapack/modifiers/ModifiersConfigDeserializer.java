package name.modid.datapack.modifiers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import name.modid.core.api.modifiers.config.ModifierConfig.AreaEffectConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.AttributeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.CustomConditionConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.MultiplyAttributeConfig;
import name.modid.datapack.modifiers.ModifiersRawConfig.ModifierRawConfigEntry;
import name.modid.datapack.modifiers.ModifiersRawConfig.ModifierType;
import name.modid.datapack.modifiers.ModifiersRawConfig.OnBlockBreakConfig;
import name.modid.datapack.modifiers.ModifiersRawConfig.OnFirstHitConfig;
import name.modid.datapack.modifiers.ModifiersRawConfig.OnHitConfig;
import name.modid.datapack.modifiers.ModifiersRawConfig.OnHitEffectConfig;

public class ModifiersConfigDeserializer implements JsonDeserializer<ModifierRawConfigEntry> {
  @Override
  public ModifierRawConfigEntry deserialize(
      JsonElement json,
      Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    JsonElement typeElement = jsonObject.get("type");
    if (typeElement == null || !typeElement.isJsonPrimitive()) {
      throw new JsonParseException(
          "Missing or invalid 'type' field in ModifierConfigEntry JSON: " + json);
    }
    String typeString = typeElement.getAsString();

    ModifierType type;
    try {
      type = ModifierType.valueOf(typeString);
    } catch (IllegalArgumentException e) {
      throw new JsonParseException(
          "Unknown modifier type: " + typeString + " in JSON: " + json,
          e);
    }

    switch (type) {
      case ON_HIT_EFFECT:
        return context.deserialize(jsonObject, OnHitEffectConfig.class);
      case ON_BLOCK_BREAK:
        return context.deserialize(jsonObject, OnBlockBreakConfig.class);
      case MULTIPLY_ATTRIBUTE:
        return context.deserialize(jsonObject, MultiplyAttributeConfig.class);
      case ATTRIBUTE:
        return context.deserialize(jsonObject, AttributeConfig.class);
      case ON_HIT:
        return context.deserialize(jsonObject, OnHitConfig.class);
      case CUSTOM_CONDITION:
        return context.deserialize(jsonObject, CustomConditionConfig.class);
      case ON_FIRST_HIT:
        return context.deserialize(jsonObject, OnFirstHitConfig.class);
      case AREA_EFFECT:
        return context.deserialize(jsonObject, AreaEffectConfig.class);
      default:
        throw new JsonParseException("Unhandled modifier type: " + typeString + " in JSON: " + json);
    }
  }
}