package name.modid.neoforge;

import name.modid.Gemstones;
import name.modid.GemstonesClient;
import name.modid.core.api.entities.jeweleryTable.JewelryTableScreen;
import name.modid.core.api.particles.BleedParticleFactory;
import name.modid.core.api.particles.ScarabParticleFactory;
import name.modid.core.api.particles.SparkParticleFactory;
import name.modid.core.api.particles.StunnedParticleFactory;
import name.modid.core.content.registries.ParticlesRegistry;
import name.modid.core.content.screen.ScreenRegistry;
import name.modid.neoforge.client.GemstonesNeoForgeClientEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(Gemstones.MOD_ID)
public class GemstonesNeoForge {
  public GemstonesNeoForge(IEventBus eventBus) {
    Gemstones.init();

    if (FMLEnvironment.dist.isClient()) {
      eventBus.addListener(this::registerParticleProviders);
      eventBus.addListener(this::clientSetup);
      // workaround for missing MenuRegistry
      eventBus.addListener(this::registerScreens);
      NeoForge.EVENT_BUS.addListener(GemstonesNeoForgeClientEvents::onItemTooltip);
      NeoForge.EVENT_BUS.addListener(GemstonesNeoForgeClientEvents::onGatherTooltipComponents);
    }
  }

  private void clientSetup(FMLClientSetupEvent event) {
    GemstonesClient.initClient();
  }

  private void registerScreens(RegisterMenuScreensEvent event) {
    event.register(
        ScreenRegistry.JEWELRY_TABLE_SCREEN_HANDLER.get(),
        JewelryTableScreen::new);
  }

  private void registerParticleProviders(RegisterParticleProvidersEvent event) {
    event.registerSpriteSet(ParticlesRegistry.BLEED_PARTICLE.get(), BleedParticleFactory::new);
    event.registerSpriteSet(ParticlesRegistry.STUNNED_PARTICLE.get(), StunnedParticleFactory::new);
    event.registerSpriteSet(ParticlesRegistry.SPARK_PARTICLE.get(), SparkParticleFactory::new);
    event.registerSpriteSet(ParticlesRegistry.SCARAB_PARTICLE.get(), ScarabParticleFactory::new);
  }
}
