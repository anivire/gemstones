package name.modid.core.api.particles;

import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import name.modid.core.content.registries.ParticlesRegistry;

public class ClientParticlesRegistry {
  public static void initialize() {
    ParticleProviderRegistry.register(ParticlesRegistry.BLEED_PARTICLE,
        BleedParticleFactory::new);

    ParticleProviderRegistry.register(ParticlesRegistry.STUNNED_PARTICLE,
        StunnedParticleFactory::new);

    ParticleProviderRegistry.register(ParticlesRegistry.SPARK_PARTICLE,
        SparkParticleFactory::new);

    ParticleProviderRegistry.register(
        ParticlesRegistry.SCARAB_PARTICLE, ScarabParticleFactory::new);
  }
}
