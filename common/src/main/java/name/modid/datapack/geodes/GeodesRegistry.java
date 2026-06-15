package name.modid.datapack.geodes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GeodesRegistry {
  private static final Map<String, GeodesConfig> CACHED_CONFIGS = new ConcurrentHashMap<>();

  public static GeodesConfig getConfig(String id) {
    return CACHED_CONFIGS.computeIfAbsent(id, key -> GeodesDataLoader.getConfigs().get(key));
  }

  public static void clearCache() {
    CACHED_CONFIGS.clear();
  }
}