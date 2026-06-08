package name.modid.datapack.sockets;

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
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class SocketSettingsDataLoader implements SimpleSynchronousResourceReloadListener {
  private static final Logger LOGGER = Gemstones.LOGGER;
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public static final Identifier ID = Identifier.of(Gemstones.MOD_ID, "socket_settings_config");
  private static SocketSettingsConfig loadedConfig = new SocketSettingsConfig();
  private static Map<String, String> loadedConfigSources = Collections.emptyMap();

  @Override
  public void reload(ResourceManager manager) {
    LOGGER.info("Reloading socket settings configs...");

    Map<String, String> newConfigSources = new LinkedHashMap<>();

    manager.findResources("sockets", id -> id.getPath().endsWith(".json"))
        .forEach((id, resource) -> {
          if (!id.getNamespace().equals(Gemstones.MOD_ID)) {
            return;
          }

          try (BufferedReader reader = new BufferedReader(
              new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            newConfigSources.put(id.toString(), reader.lines().collect(Collectors.joining(System.lineSeparator())));
          } catch (Exception e) {
            LOGGER.error("Error loading socket settings config {}: {}", id, e.getMessage());
          }
        });

    loadedConfig = parseConfigs(newConfigSources);
    loadedConfigSources = Collections.unmodifiableMap(new LinkedHashMap<>(newConfigSources));

    LOGGER.info("Finished reloading socket settings configs.");
  }

  public static int getMaxSlots() {
    return loadedConfig.maxSlots;
  }

  public static Map<String, String> getLoadedConfigSources() {
    return loadedConfigSources;
  }

  public static void applySyncedConfigs(Map<String, String> configSources) {
    Map<String, String> copiedSources = new LinkedHashMap<>(configSources);
    loadedConfig = parseConfigs(copiedSources);
    loadedConfigSources = Collections.unmodifiableMap(copiedSources);
    LOGGER.info("Synced socket settings configs from server.");
  }

  private static SocketSettingsConfig parseConfigs(Map<String, String> configSources) {
    SocketSettingsConfig merged = new SocketSettingsConfig();

    configSources.forEach((id, fileContent) -> {
      try {
        merged.merge(GSON.fromJson(fileContent, SocketSettingsConfig.class));
      } catch (Exception e) {
        LOGGER.error("Error parsing socket settings config {}: {}", id, e.getMessage());
      }
    });

    return merged;
  }

  @Override
  public Identifier getFabricId() {
    return ID;
  }
}
