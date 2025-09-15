package name.modid.core.api.particles;

import name.modid.core.content.registries.ParticlesRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class ClientParticlesRegistry {
  public static void initialize() {
    ParticleFactoryRegistry.getInstance().register(ParticlesRegistry.BLEED_PARTICLE,
        BleedParticleFactory::new);

    ParticleFactoryRegistry.getInstance().register(ParticlesRegistry.STUNNED_PARTICLE,
        StunnedParticleFactory::new);

    ParticleFactoryRegistry.getInstance().register(
        ParticlesRegistry.SCARAB_PARTICLE, ScarabParticleFactory::new);
  }
}
