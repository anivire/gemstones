package name.modid.core.content.registries;

import com.mojang.serialization.MapCodec;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import name.modid.Gemstones;
import name.modid.core.content.particles.ScarabParticleInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryKeys;

public class ParticlesRegistry {
  public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Gemstones.MOD_ID,
      RegistryKeys.PARTICLE_TYPE);

  public static final RegistrySupplier<SimpleParticleType> BLEED_PARTICLE = PARTICLES.register("bleed_particle",
      () -> new SimpleParticleType(false) {
      });

  public static final RegistrySupplier<SimpleParticleType> STUNNED_PARTICLE = PARTICLES.register("stunned_particle",
      () -> new SimpleParticleType(false) {
      });

  public static final RegistrySupplier<SimpleParticleType> SPARK_PARTICLE = PARTICLES.register("spark_particle",
      () -> new SimpleParticleType(false) {
      });

  public static final RegistrySupplier<ParticleType<ScarabParticleInstance>> SCARAB_PARTICLE = PARTICLES
      .register("scarab_particle", () -> new ParticleType<ScarabParticleInstance>(false) {
        @Override
        public MapCodec<ScarabParticleInstance> getCodec() {
          return ScarabParticleInstance.CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ScarabParticleInstance> getPacketCodec() {
          return ScarabParticleInstance.PACKET_CODEC;
        }
      });

  public static void initialize() {
    PARTICLES.register();
  }
}