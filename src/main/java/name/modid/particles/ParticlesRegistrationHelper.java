package name.modid.particles;

import com.mojang.serialization.MapCodec;

import name.modid.Gemstones;
import name.modid.helpers.particles.ScarabParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ParticlesRegistrationHelper {
  public static final SimpleParticleType BLEED_PARTICLE = FabricParticleTypes.simple();
  public static final SimpleParticleType STUNNED_PARTICLE = FabricParticleTypes.simple();
  public static final ParticleType<ScarabParticleEffect> SCARAB_PARTICLE = new ParticleType<ScarabParticleEffect>(
      false) {
    @Override
    public MapCodec<ScarabParticleEffect> getCodec() {
      return ScarabParticleEffect.CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ScarabParticleEffect> getPacketCodec() {
      return ScarabParticleEffect.PACKET_CODEC;
    }
  };

  public static void initialize() {
    Registry.register(Registries.PARTICLE_TYPE,
        Identifier.of(Gemstones.MOD_ID, "bleed_particle"), BLEED_PARTICLE);

    Registry.register(Registries.PARTICLE_TYPE,
        Identifier.of(Gemstones.MOD_ID, "stunned_particle"), STUNNED_PARTICLE);

    Registry.register(Registries.PARTICLE_TYPE,
        Identifier.of(Gemstones.MOD_ID, "scarab_particle"), SCARAB_PARTICLE);
  }
}