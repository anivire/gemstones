package name.modid.datapack.items;

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

public class ItemCompatibilityDataLoader implements SynchronousResourceReloader {
  private static final Logger LOGGER = Gemstones.LOGGER;
  private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter())
      .setPrettyPrinting()
      .create();

  public static final Identifier ID = Identifier.of(Gemstones.MOD_ID, "compatibility_data_config");
  private static Map<String, ItemCompatibilityConfig> loadedConfigs = Collections.emptyMap();
  private static Map<String, String> loadedConfigSources = Collections.emptyMap();

  @Override
  public void reload(ResourceManager manager) {
    LOGGER.info("Reloading item compatibility configs...");

    Map<String, String> newConfigSources = new LinkedHashMap<>();

    manager.findResources("items", id -> id.getPath().endsWith(".json"))
        .forEach((id, resource) -> {
          try (BufferedReader reader = new BufferedReader(
              new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            newConfigSources.put(id.toString(), reader.lines().collect(Collectors.joining(System.lineSeparator())));
          } catch (Exception e) {
            LOGGER.error("Error loading item compatibility config {}: {}", id, e.getMessage());
          }
        });

    loadedConfigs = Collections.unmodifiableMap(parseConfigs(newConfigSources));
    loadedConfigSources = Collections.unmodifiableMap(new LinkedHashMap<>(newConfigSources));
    ItemCompatibilityRegistry.rebuild(loadedConfigs);

    LOGGER.info("Finished reloading item compatibility configs. Loaded {} entries.", loadedConfigs.size());
  }

  public static Map<String, String> getLoadedConfigSources() {
    return loadedConfigSources;
  }

  public static void applySyncedConfigs(Map<String, String> configSources) {
    Map<String, String> copiedSources = new LinkedHashMap<>(configSources);
    loadedConfigs = Collections.unmodifiableMap(parseConfigs(copiedSources));
    loadedConfigSources = Collections.unmodifiableMap(copiedSources);
    ItemCompatibilityRegistry.rebuild(loadedConfigs);
    LOGGER.info("Synced item compatibility configs from server. Loaded {} entries.", loadedConfigs.size());
  }

  private static Map<String, ItemCompatibilityConfig> parseConfigs(Map<String, String> configSources) {
    Map<String, ItemCompatibilityConfig> newConfigs = new LinkedHashMap<>();

    configSources.forEach((id, fileContent) -> {
      try {
        ItemCompatibilityConfig config = GSON.fromJson(fileContent, ItemCompatibilityConfig.class);

        if (isValid(config, id)) {
          newConfigs.put(id, config);
        }
      } catch (Exception e) {
        LOGGER.error("Error parsing item compatibility config {}: {}", id, e.getMessage());
      }
    });

    return newConfigs;
  }

  private static boolean isValid(ItemCompatibilityConfig config, String id) {
    if (config == null) {
      LOGGER.warn("Skipping empty item compatibility config {}", id);
      return false;
    }

    boolean hasTargets = !config.items.isEmpty() || !config.tags.isEmpty();
    if (!hasTargets) {
      LOGGER.warn("Skipping item compatibility config {} without items or tags", id);
      return false;
    }

    if (!config.blacklist && config.category == null) {
      LOGGER.warn("Skipping whitelist item compatibility config {} without category", id);
      return false;
    }

    return true;
  }

}
