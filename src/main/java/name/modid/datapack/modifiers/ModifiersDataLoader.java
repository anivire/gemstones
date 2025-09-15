package name.modid.datapack.modifiers;

import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.datapack.core.IdentifierTypeAdapter;
import name.modid.datapack.modifiers.ModifiersConfig.ModifierConfigEntry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class ModifiersDataLoader implements SimpleSynchronousResourceReloadListener {
  private static final Logger LOGGER = Gemstones.LOGGER;
  private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(ModifierConfigEntry.class, new ModifiersConfigDeserializer())
      .registerTypeAdapter(Identifier.class, new IdentifierTypeAdapter())
      .setPrettyPrinting()
      .disableHtmlEscaping()
      .create();

  public static final Identifier ID = Identifier.of(Gemstones.MOD_ID,
      "gemstones_data_config");
  private static Map<GemstoneType, ModifiersConfig> loadedGemstoneConfigs = Collections.emptyMap();

  @Override
  public void reload(ResourceManager manager) {
    LOGGER.info("Reloading gemstone modifiers configs...");
    Map<GemstoneType, ModifiersConfig> newConfigs = new HashMap<>();

    manager.findResources("", path -> {
      return true;
    })
        // TODO: proper namespace gathering
        // manager.findResources("gemstones", path -> path.getPath().endsWith(".json"))
        .forEach((id, resource) -> {
          LOGGER.debug(resource.toString());
          if (id.getNamespace().equals(Gemstones.MOD_ID)) {
            LOGGER.debug(resource.toString());
            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
              ModifiersConfig config = GSON.fromJson(reader, ModifiersConfig.class);

              if (config != null) {
                if (config.gemstoneType != null) {
                  newConfigs.put(config.gemstoneType, config);
                  LOGGER.debug("Loaded config for gemstone: {}", config.gemstoneType);
                } else {
                  LOGGER.warn(
                      "Gemstone config file {} is missing 'gemstone_type' field, skipping.", id);
                }
              } else {
                LOGGER.warn("Gemstone config file {} could not be parsed, skipping.", id);
              }
            } catch (Exception e) {
              LOGGER.error("Error loading gemstone config {}: {}", id, e.getMessage());
            }
          }
        });

    loadedGemstoneConfigs = Collections.unmodifiableMap(newConfigs);
    LOGGER.info("Finished reloading gemstone modifiers configs. Loaded {} entries.",
        loadedGemstoneConfigs.size());

    ModifiersRegistry.clearCache();
  }

  public static Map<GemstoneType, ModifiersConfig> getLoadedConfigs() {
    return loadedGemstoneConfigs;
  }

  @Override
  public Identifier getFabricId() {
    return ID;
  }
}