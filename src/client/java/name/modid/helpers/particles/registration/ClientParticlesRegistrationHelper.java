package name.modid.helpers.particles.registration;

import name.modid.helpers.particles.BleedParticleFactory;
import name.modid.helpers.particles.ScarabParticleFactory;
import name.modid.helpers.particles.StunnedParticleFactory;
import name.modid.particles.ParticlesRegistrationHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class ClientParticlesRegistrationHelper {
  public static void initialize() {
    ParticleFactoryRegistry.getInstance().register(ParticlesRegistrationHelper.BLEED_PARTICLE,
        BleedParticleFactory::new);

    ParticleFactoryRegistry.getInstance().register(ParticlesRegistrationHelper.STUNNED_PARTICLE,
        StunnedParticleFactory::new);

    ParticleFactoryRegistry.getInstance().register(
        ParticlesRegistrationHelper.SCARAB_PARTICLE, ScarabParticleFactory::new);
  }
}
