package name.modid.fabric;

import name.modid.GemstonesClient;
import name.modid.core.api.particles.BleedParticleFactory;
import name.modid.core.api.particles.ScarabParticleFactory;
import name.modid.core.api.particles.SparkParticleFactory;
import name.modid.core.api.particles.StunnedParticleFactory;
import name.modid.core.content.registries.ParticlesRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class GemstonesFabricClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    registerParticleFactories();
    GemstonesClient.initClient();
  }

  private static void registerParticleFactories() {
    ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
    registry.register(ParticlesRegistry.BLEED_PARTICLE.get(), BleedParticleFactory::new);
    registry.register(ParticlesRegistry.STUNNED_PARTICLE.get(), StunnedParticleFactory::new);
    registry.register(ParticlesRegistry.SPARK_PARTICLE.get(), SparkParticleFactory::new);
    registry.register(ParticlesRegistry.SCARAB_PARTICLE.get(), ScarabParticleFactory::new);
  }
}
