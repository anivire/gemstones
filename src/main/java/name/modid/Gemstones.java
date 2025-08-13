package name.modid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import name.modid.entities.EffectRegistrationHelper;
import name.modid.helpers.ItemRegistrationHelper;
import name.modid.helpers.attributes.AttributeRegistrationHelper;
import name.modid.helpers.components.ComponentsHelper;
import name.modid.helpers.events.EventRegistrationHelper;
import name.modid.helpers.particles.ParticlesRegistrationHelper;
import net.fabricmc.api.ModInitializer;

public class Gemstones implements ModInitializer {
  public static final String MOD_ID = "gemstones";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    LOGGER.info("Initializing Gemstones");
    AttributeRegistrationHelper.initialize();
    ComponentsHelper.initialize();
    EventRegistrationHelper.initialize();
    ItemRegistrationHelper.initialize();
    EffectRegistrationHelper.initialize();
    ParticlesRegistrationHelper.initialize();
  }
}
