package name.modid.datapack.core;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.util.Identifier;

public class IdentifierTypeAdapter implements JsonSerializer<Identifier>, JsonDeserializer<Identifier> {

  @Override
  public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
      return Identifier.of(json.getAsString());
    }
    throw new JsonParseException("Expected a string for Identifier but got: " + json.toString());
  }

  @Override
  public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.toString());
  }
}