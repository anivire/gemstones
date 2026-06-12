package name.modid.datapack.drops;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import name.modid.Gemstones;
import name.modid.datapack.core.IdentifierTypeAdapter;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;

public class DropsDataLoader implements SynchronousResourceReloader {
  private static final Logger LOGGER = Gemstones.LOGGER;
  private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter())
      .setPrettyPrinting()
      .create();

  public static final Identifier ID = Identifier.of(Gemstones.MOD_ID, "drops_data_config");
  private static DropsConfig loadedConfig = new DropsConfig();
  private static Map<String, String> loadedConfigSources = Collections.emptyMap();

  @Override
  public void reload(ResourceManager manager) {
    LOGGER.info("Reloading drops configs...");

    Map<String, String> newConfigSources = new LinkedHashMap<>();

    manager.findResources("drops", id -> id.getPath().endsWith(".json"))
        .forEach((id, resource) -> {
          if (!id.getNamespace().equals(Gemstones.MOD_ID)) {
            return;
          }

          try (BufferedReader reader = new BufferedReader(
              new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            newConfigSources.put(id.toString(), reader.lines().collect(Collectors.joining(System.lineSeparator())));
          } catch (Exception e) {
            LOGGER.error("Error loading drops config {}: {}", id, e.getMessage());
          }
        });

    DropsConfig config = parseConfigs(newConfigSources);
    loadedConfig = config;
    loadedConfigSources = Collections.unmodifiableMap(new LinkedHashMap<>(newConfigSources));

    LOGGER.info("Finished reloading drops configs.");
    DropsRegistry.clearCache();
  }

  public static DropsConfig getConfig() {
    return loadedConfig;
  }

  public static Map<String, String> getLoadedConfigSources() {
    return loadedConfigSources;
  }

  public static void applySyncedConfigs(Map<String, String> configSources) {
    Map<String, String> copiedSources = new LinkedHashMap<>(configSources);
    loadedConfig = parseConfigs(copiedSources);
    loadedConfigSources = Collections.unmodifiableMap(copiedSources);
    DropsRegistry.clearCache();
    LOGGER.info("Synced drops configs from server.");
  }

  private static DropsConfig parseConfigs(Map<String, String> configSources) {
    DropsConfig merged = new DropsConfig();

    configSources.forEach((id, fileContent) -> {
      try {
        DropsConfig partial = GSON.fromJson(fileContent, DropsConfig.class);

        if (partial != null) {
          merged.merge(partial);
        }
      } catch (Exception e) {
        LOGGER.error("Error parsing drops config {}: {}", id, e.getMessage());
      }
    });

    merged.seal();
    return merged;
  }

}
