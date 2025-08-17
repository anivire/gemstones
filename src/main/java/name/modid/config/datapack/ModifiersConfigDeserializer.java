package name.modid.config.datapack;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import name.modid.config.datapack.ModifiersConfig.ModifierConfigEntry;
import name.modid.config.datapack.ModifiersConfig.ModifierType;
import name.modid.config.datapack.ModifiersConfig.MultiplyAttributeConfig;
import name.modid.config.datapack.ModifiersConfig.OnBlockBreakConfig;
import name.modid.config.datapack.ModifiersConfig.OnHitEffectConfig;

public class ModifiersConfigDeserializer implements JsonDeserializer<ModifierConfigEntry> {
  @Override
  public ModifierConfigEntry deserialize(
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
      default:
        throw new JsonParseException("Unhandled modifier type: " + typeString + " in JSON: " + json);
    }
  }
}