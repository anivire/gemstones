package name.modid.core.content.registries;

import com.mojang.serialization.MapCodec;

import name.modid.Gemstones;
import name.modid.core.content.particles.ScarabParticleInstance;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ParticlesRegistry {
  public static final SimpleParticleType BLEED_PARTICLE = FabricParticleTypes.simple();
  public static final SimpleParticleType STUNNED_PARTICLE = FabricParticleTypes.simple();
  public static final SimpleParticleType SPARK_PARTICLE = FabricParticleTypes.simple();

  public static final ParticleType<ScarabParticleInstance> SCARAB_PARTICLE = new ParticleType<ScarabParticleInstance>(
      false) {
    @Override
    public MapCodec<ScarabParticleInstance> getCodec() {
      return ScarabParticleInstance.CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ScarabParticleInstance> getPacketCodec() {
      return ScarabParticleInstance.PACKET_CODEC;
    }
  };

  public static void initialize() {
    Registry.register(Registries.PARTICLE_TYPE,
        Identifier.of(Gemstones.MOD_ID, "bleed_particle"), BLEED_PARTICLE);

    Registry.register(Registries.PARTICLE_TYPE,
        Identifier.of(Gemstones.MOD_ID, "stunned_particle"), STUNNED_PARTICLE);

    Registry.register(Registries.PARTICLE_TYPE,
        Identifier.of(Gemstones.MOD_ID, "scarab_particle"), SCARAB_PARTICLE);

    Registry.register(Registries.PARTICLE_TYPE,
        Identifier.of(Gemstones.MOD_ID, "spark_particle"), SPARK_PARTICLE);
  }
}