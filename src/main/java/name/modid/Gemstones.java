package name.modid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import name.modid.config.datapack.ModifiersDataLoader;
import name.modid.effects.registration.EffectRegistrationHelper;
import name.modid.helpers.ItemRegistrationHelper;
import name.modid.helpers.TagsRegistrationHelper;
import name.modid.helpers.attributes.AttributeRegistrationHelper;
import name.modid.helpers.components.ComponentsHelper;
import name.modid.helpers.events.EventRegistrationHelper;
import name.modid.particles.ParticlesRegistrationHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class Gemstones implements ModInitializer {
  public static final String MOD_ID = "gemstones";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    LOGGER.info("Initializing Gemstones");

    ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ModifiersDataLoader());

    AttributeRegistrationHelper.initialize();
    ComponentsHelper.initialize();
    TagsRegistrationHelper.initialize();
    EventRegistrationHelper.initialize();
    ItemRegistrationHelper.initialize();
    EffectRegistrationHelper.initialize();
    ParticlesRegistrationHelper.initialize();
  }
}
