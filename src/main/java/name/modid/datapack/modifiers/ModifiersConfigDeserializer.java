package name.modid.datapack.modifiers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import name.modid.datapack.modifiers.ModifiersRawConfig.ModifierRawConfigEntry;
import name.modid.datapack.modifiers.ModifiersRawConfig.ModifierType;

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

    return switch (type) {
      case ON_HIT_EFFECT ->
        context.deserialize(jsonObject, ModifiersRawConfig.OnHitEffectConfig.class);
      case ON_BLOCK_BREAK ->
        context.deserialize(jsonObject, ModifiersRawConfig.OnBlockBreakConfig.class);
      case MULTIPLY_ATTRIBUTE ->
        context.deserialize(jsonObject, ModifiersRawConfig.MultiplyAttributeConfig.class);
      case ATTRIBUTE ->
        context.deserialize(jsonObject, ModifiersRawConfig.AttributeConfig.class);
      case ON_HIT ->
        context.deserialize(jsonObject, ModifiersRawConfig.OnHitConfig.class);
      case CUSTOM_CONDITION ->
        context.deserialize(jsonObject, ModifiersRawConfig.CustomConditionConfig.class);
      case ON_FIRST_HIT ->
        context.deserialize(jsonObject, ModifiersRawConfig.OnFirstHitConfig.class);
      case AREA_EFFECT ->
        context.deserialize(jsonObject, ModifiersRawConfig.AreaEffectConfig.class);
    };
  }
}