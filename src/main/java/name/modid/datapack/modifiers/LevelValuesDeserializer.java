package name.modid.datapack.modifiers;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import name.modid.core.api.modifiers.types.LevelValues;

public class LevelValuesDeserializer implements JsonDeserializer<LevelValues> {

  @Override
  public LevelValues deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (!json.isJsonArray()) {
      throw new JsonParseException("Expected a JSON array for LevelValues");
    }

    JsonArray jsonArray = json.getAsJsonArray();
    ArrayList<Double> values = new ArrayList<>();

    for (JsonElement element : jsonArray) {
      if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
        values.add(element.getAsDouble());
      } else {
        throw new JsonParseException("Expected a number in LevelValues array, but got: " + element);
      }
    }

    return new LevelValues(values);
  }
}