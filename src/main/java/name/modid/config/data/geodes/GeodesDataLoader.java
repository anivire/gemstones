package name.modid.config.data.geodes;

import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import name.modid.Gemstones;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class GeodesDataLoader implements SimpleSynchronousResourceReloadListener {
  private static final Logger LOGGER = Gemstones.LOGGER;
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public static final Identifier ID = Identifier.of(Gemstones.MOD_ID, "geodes_data_config");
  private static Map<String, GeodesConfig> loadedGeodeConfigs = Collections.emptyMap();

  @Override
  public void reload(ResourceManager manager) {
    LOGGER.info("[GeodesConfig] Reloading geode configs...");

    Map<String, GeodesConfig> newConfigs = new HashMap<>();

    manager.findResources("", path -> {
      return true;
    })
        .forEach((id, resource) -> {
          // TODO: proper namespace gathering
          LOGGER.debug(resource.toString());
          if (id.getNamespace().equals(Gemstones.MOD_ID)) {
            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
              GeodesConfig config = GSON.fromJson(reader, GeodesConfig.class);
              if (config != null && config.geodeId != null) {
                newConfigs.put(config.geodeId, config);
                LOGGER.debug("Loaded geode config {}", config.geodeId);
              } else {
                LOGGER.warn("Skipping invalid geode config: {}", id);
              }
            } catch (Exception e) {
              LOGGER.error("Error loading geode config {}: {}", id, e.getMessage());
            }
          }
        });

    loadedGeodeConfigs = Collections.unmodifiableMap(newConfigs);

    LOGGER.info("[GeodesConfig] Finished. Loaded {}", loadedGeodeConfigs.size());
    GeodesRegistry.clearCache();
  }

  public static Map<String, GeodesConfig> getConfigs() {
    return loadedGeodeConfigs;
  }

  @Override
  public Identifier getFabricId() {
    return ID;
  }
}