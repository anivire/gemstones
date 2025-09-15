package name.modid.core.content.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import name.modid.core.content.registries.ParticlesRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class ScarabParticleInstance implements ParticleEffect {
  public final int entityId;

  public ScarabParticleInstance(int entityId) {
    this.entityId = entityId;
  }

  @Override
  public ParticleType<?> getType() {
    return ParticlesRegistry.SCARAB_PARTICLE;
  }

  public static final MapCodec<ScarabParticleInstance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      Codec.INT.fieldOf("entity_id").forGetter(effect -> effect.entityId))
      .apply(instance, ScarabParticleInstance::new));

  public static final PacketCodec<RegistryByteBuf, ScarabParticleInstance> PACKET_CODEC = PacketCodec.tuple(
      PacketCodecs.INTEGER, effect -> effect.entityId,
      ScarabParticleInstance::new);
}