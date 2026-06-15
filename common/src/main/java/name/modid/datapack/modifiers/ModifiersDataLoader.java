package name.modid.datapack.modifiers;

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
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.LevelValues;
import name.modid.datapack.core.IdentifierTypeAdapter;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;

public class ModifiersDataLoader implements SynchronousResourceReloader {
  private static final Logger LOGGER = Gemstones.LOGGER;
  private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(ModifiersRawConfig.class, new ModifiersRawConfigDeserializer())
      .registerTypeAdapter(ModifierConfig.class, new ModifiersConfigDeserializer())
      .registerTypeAdapter(LevelValues.class, new LevelValuesDeserializer())
      .registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter())
      .setPrettyPrinting()
      .create();

  public static final Identifier ID = Identifier.of(Gemstones.MOD_ID,
      "gemstones_data_config");
  private static Map<GemstoneType, ModifiersRawConfig> loadedGemstoneConfigs = Collections.emptyMap();
  private static Map<String, String> loadedGemstoneConfigSources = Collections.emptyMap();

  @Override
  public void reload(ResourceManager manager) {
    LOGGER.info("Reloading gemstone modifiers configs...");
    Map<String, String> newConfigSources = new LinkedHashMap<>();

    manager.findResources(Gemstones.MOD_ID, id -> id.getPath().endsWith(".json"))
        .forEach((id, resource) -> {
          if (!id.getNamespace().equals(Gemstones.MOD_ID)) {
            return;
          }

          try {
            String fileContent;
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
              fileContent = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }

            newConfigSources.put(id.toString(), fileContent);
          } catch (Exception e) {
            LOGGER.error("Failed to load gemstone config {}: {}", id, e.getMessage());
          }
        });

    Map<GemstoneType, ModifiersRawConfig> newConfigs = parseConfigs(newConfigSources);
    loadedGemstoneConfigs = Collections.unmodifiableMap(newConfigs);
    loadedGemstoneConfigSources = Collections.unmodifiableMap(new LinkedHashMap<>(newConfigSources));
    LOGGER.info("Finished reloading gemstone modifiers configs. Loaded {} entries.",
        loadedGemstoneConfigs.size());

    ModifiersRegistry.clearCache();
  }

  public static Map<GemstoneType, ModifiersRawConfig> getLoadedConfigs() {
    return loadedGemstoneConfigs;
  }

  public static Map<String, String> getLoadedConfigSources() {
    return loadedGemstoneConfigSources;
  }

  public static void applySyncedConfigs(Map<String, String> configSources) {
    Map<String, String> copiedSources = new LinkedHashMap<>(configSources);
    loadedGemstoneConfigs = Collections.unmodifiableMap(parseConfigs(copiedSources));
    loadedGemstoneConfigSources = Collections.unmodifiableMap(copiedSources);
    ModifiersRegistry.clearCache();
    LOGGER.info("Synced gemstone modifiers configs from server. Loaded {} entries.",
        loadedGemstoneConfigs.size());
  }

  private static Map<GemstoneType, ModifiersRawConfig> parseConfigs(Map<String, String> configSources) {
    Map<GemstoneType, ModifiersRawConfig> newConfigs = new HashMap<>();

    configSources.forEach((id, fileContent) -> {
      try {
        ModifiersRawConfig config = GSON.fromJson(fileContent, ModifiersRawConfig.class);

        if (config != null) {
          if (config.gemstone_type != null) {
            newConfigs.put(config.gemstone_type, config);
            LOGGER.debug("Loaded config for gemstone: {}", config.gemstone_type);
          } else {
            LOGGER.warn(
                "Gemstone config file {} is missing 'gemstone_type' field, skipping.", id);
          }
        } else {
          LOGGER.warn("Gemstone config file {} could not be parsed, skipping.", id);
        }
      } catch (Exception e) {
        LOGGER.error("Failed to load gemstone config {}: {}", id, e.getMessage());
      }
    });

    return newConfigs;
  }

}
