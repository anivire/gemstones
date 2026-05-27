package name.modid.datapack.geodes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import name.modid.Gemstones;
import name.modid.datapack.core.IdentifierTypeAdapter;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class GeodesDataLoader implements SimpleSynchronousResourceReloadListener {
  private static final Logger LOGGER = Gemstones.LOGGER;
  private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter())
      .setPrettyPrinting()
      .create();

  public static final Identifier ID = Identifier.of(Gemstones.MOD_ID, "geodes_data_config");
  private static Map<String, GeodesConfig> loadedGeodeConfigs = Collections.emptyMap();
  private static Map<String, String> loadedGeodeConfigSources = Collections.emptyMap();

  @Override
  public void reload(ResourceManager manager) {
    LOGGER.info("Reloading geode configs...");

    Map<String, String> newConfigSources = new LinkedHashMap<>();

    manager.findResources("geodes", id -> id.getPath().endsWith(".json"))
        .forEach((id, resource) -> {
          if (!id.getNamespace().equals(Gemstones.MOD_ID)) {
            return;
          }

          try (BufferedReader reader = new BufferedReader(
              new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            newConfigSources.put(id.toString(), reader.lines().collect(Collectors.joining(System.lineSeparator())));
          } catch (Exception e) {
            LOGGER.error("Error loading geode config {}: {}", id, e.getMessage());
          }
        });

    Map<String, GeodesConfig> newConfigs = parseConfigs(newConfigSources);
    loadedGeodeConfigs = Collections.unmodifiableMap(newConfigs);
    loadedGeodeConfigSources = Collections.unmodifiableMap(new LinkedHashMap<>(newConfigSources));

    LOGGER.info("Finished. Loaded {}", loadedGeodeConfigs.size());
    GeodesRegistry.clearCache();
  }

  public static Map<String, GeodesConfig> getConfigs() {
    return loadedGeodeConfigs;
  }

  public static Map<String, String> getLoadedConfigSources() {
    return loadedGeodeConfigSources;
  }

  public static void applySyncedConfigs(Map<String, String> configSources) {
    Map<String, String> copiedSources = new LinkedHashMap<>(configSources);
    loadedGeodeConfigs = Collections.unmodifiableMap(parseConfigs(copiedSources));
    loadedGeodeConfigSources = Collections.unmodifiableMap(copiedSources);
    GeodesRegistry.clearCache();
    LOGGER.info("Synced geode configs from server. Loaded {} entries.", loadedGeodeConfigs.size());
  }

  private static Map<String, GeodesConfig> parseConfigs(Map<String, String> configSources) {
    Map<String, GeodesConfig> newConfigs = new HashMap<>();

    configSources.forEach((id, fileContent) -> {
      try {
        GeodesConfig config = GSON.fromJson(fileContent, GeodesConfig.class);

        if (config != null && config.geodeId != null) {
          config.normalize();
          newConfigs.put(config.geodeId, config);
          LOGGER.debug("Loaded geode config {}", config.geodeId);
        } else {
          LOGGER.warn("Skipping invalid geode config: {}", id);
        }
      } catch (Exception e) {
        LOGGER.error("Error loading geode config {}: {}", id, e.getMessage());
      }
    });

    return newConfigs;
  }

  @Override
  public Identifier getFabricId() {
    return ID;
  }
}
