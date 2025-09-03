package name.modid.particles.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import name.modid.particles.ParticlesRegistrationHelper;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class ScarabParticleEffect implements ParticleEffect {
  public final int entityId;

  public ScarabParticleEffect(int entityId) {
    this.entityId = entityId;
  }

  @Override
  public ParticleType<?> getType() {
    return ParticlesRegistrationHelper.SCARAB_PARTICLE;
  }

  public static final MapCodec<ScarabParticleEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      Codec.INT.fieldOf("entity_id").forGetter(effect -> effect.entityId)).apply(instance, ScarabParticleEffect::new));

  public static final PacketCodec<RegistryByteBuf, ScarabParticleEffect> PACKET_CODEC = PacketCodec.tuple(
      PacketCodecs.INTEGER, effect -> effect.entityId,
      ScarabParticleEffect::new);
}