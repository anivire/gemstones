package name.modid.datapack.modifiers;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;

public class ModifiersRawConfigDeserializer implements JsonDeserializer<ModifiersRawConfig> {

  @Override
  public ModifiersRawConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject obj = json.getAsJsonObject();
    GemstoneType gemstoneType = context.deserialize(obj.get("gemstone_type"), GemstoneType.class);
    Map<ModifierItemCategory, ModifierConfig> modifiers = new HashMap<>();

    if (obj.has("modifiers") && obj.get("modifiers").isJsonObject()) {
      JsonObject modifiersObject = obj.getAsJsonObject("modifiers");

      for (Map.Entry<String, JsonElement> entry : modifiersObject.entrySet()) {
        try {
          ModifierItemCategory category = ModifierItemCategory.valueOf(entry.getKey());
          ModifierConfig config = context.deserialize(entry.getValue(), ModifierConfig.class);

          modifiers.put(category, config);
        } catch (IllegalArgumentException e) {
          Gemstones.LOGGER.warn("Skipping invalid modifier category '{}' in config for gemstone '{}'", entry.getKey(),
              gemstoneType);
        } catch (JsonParseException e) {
          Gemstones.LOGGER.warn("Skipping invalid modifier config for category '{}' in gemstone '{}'. Reason: {}",
              entry.getKey(), gemstoneType, e.getMessage());
        }
      }
    }

    return new ModifiersRawConfig(gemstoneType, modifiers);
  }
}