package name.modid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.modid.core.api.attributes.AttributesRegistry;
import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.api.effects.EffectsRegistry;
import name.modid.core.api.events.EventsRegistry;
import name.modid.core.api.particles.ParticlesRegistry;
import name.modid.core.api.tags.TagsRegistry;
import name.modid.core.content.items.registries.ItemsRegistry;
import name.modid.datapack.geodes.GeodesDataLoader;
import name.modid.datapack.modifiers.ModifiersDataLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class Gemstones implements ModInitializer {
  public static final String MOD_ID = "gemstones";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
  public static boolean ALT_STYLE = true;

  @Override
  public void onInitialize() {
    LOGGER.info("Initializing Gemstones");

    ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ModifiersDataLoader());
    ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new GeodesDataLoader());

    AttributesRegistry.initialize();
    ComponentsRegistry.initialize();
    TagsRegistry.initialize();
    EventsRegistry.initialize();
    ItemsRegistry.initialize();
    EffectsRegistry.initialize();
    ParticlesRegistry.initialize();
  }
}
