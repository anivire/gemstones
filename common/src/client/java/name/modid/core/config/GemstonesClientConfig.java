package name.modid.core.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import dev.architectury.platform.Platform;
import name.modid.Gemstones;

public final class GemstonesClientConfig {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final String FILE_NAME = Gemstones.MOD_ID + "-client.json";

  private static Values values = new Values();

  private GemstonesClientConfig() {
  }

  public static final class Values {
    public boolean showEmptySockets = true;
  }

  public static Values get() {
    return values;
  }

  public static void load() {
    Path path = getPath();
    if (Files.exists(path)) {
      try (Reader reader = Files.newBufferedReader(path)) {
        Values loaded = GSON.fromJson(reader, Values.class);
        if (loaded != null) {
          values = loaded;
        }
      } catch (IOException | JsonParseException e) {
        Gemstones.LOGGER.warn("Could not read {}, using defaults", path, e);
      }
    }

    save();
  }

  public static void save() {
    Path path = getPath();
    try {
      Files.createDirectories(path.getParent());
      try (Writer writer = Files.newBufferedWriter(path)) {
        GSON.toJson(values, writer);
      }
    } catch (IOException e) {
      Gemstones.LOGGER.warn("Could not save {}", path, e);
    }
  }

  private static Path getPath() {
    return Platform.getConfigFolder().resolve(FILE_NAME);
  }
}
