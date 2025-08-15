package name.modid.helpers.particles;

import name.modid.particles.ParticlesRegistrationHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class ClientParticlesRegistrationHelper {
  public static void initialize() {
    ParticleFactoryRegistry.getInstance().register(ParticlesRegistrationHelper.BLEED_PARTICLE,
        BleedParticleFactory::new);

    ParticleFactoryRegistry.getInstance().register(ParticlesRegistrationHelper.STUNNED_PARTICLE,
        StunnedParticleFactory::new);
  }
}
