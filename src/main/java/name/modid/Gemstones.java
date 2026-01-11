package name.modid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.content.blocks.BlocksRegistry;
import name.modid.core.content.blocks.entity.core.BlockEntitiesRegistry;
import name.modid.core.content.items.registries.ItemsRegistry;
import name.modid.core.content.loot.LootConditionRegistry;
import name.modid.core.content.registries.AttachmentsRegistry;
import name.modid.core.content.registries.AttributesRegistry;
import name.modid.core.content.registries.EffectsRegistry;
import name.modid.core.content.registries.EventsRegistry;
import name.modid.core.content.registries.ParticlesRegistry;
import name.modid.core.content.registries.TagsRegistry;
import name.modid.core.content.screen.ScreenRegistry;
import name.modid.core.network.NetworkHandler;
import name.modid.datagen.RecourceHandler;
import net.fabricmc.api.ModInitializer;

public class Gemstones implements ModInitializer {
  public static final String MOD_ID = "gemstones";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
  public static boolean ALT_STYLE = true;

  @Override
  public void onInitialize() {
    LOGGER.info("Initializing Gemstones");

    RecourceHandler.initialize();
    NetworkHandler.initialize();

    TagsRegistry.initialize();
    LootConditionRegistry.initialize();

    EventType.initialize();
    AttributesRegistry.initialize();
    ComponentsRegistry.initialize();
    EventsRegistry.initialize();
    ItemsRegistry.initialize();
    EffectsRegistry.initialize();
    ParticlesRegistry.initialize();
    AttachmentsRegistry.initialize();
    BlocksRegistry.initialize();
    BlockEntitiesRegistry.initialize();
    ScreenRegistry.initialize();
  }
}
