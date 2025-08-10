package name.modid.helpers.particles;

import name.modid.Gemstones;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ParticlesRegistrationHelper {
  public static final SimpleParticleType BLEED_PARTICLE = FabricParticleTypes.simple();
  public static final SimpleParticleType STUNNED_PARTICLE = FabricParticleTypes.simple();

  public static void initialize() {
    Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Gemstones.MOD_ID, "bleed_particle"),
        BLEED_PARTICLE);

    Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Gemstones.MOD_ID, "stunned_particle"),
        STUNNED_PARTICLE);
  }
}
