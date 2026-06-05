package name.modid.core.api.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record PolishingComponent(int completedStages, int ticksInStage, int stageDuration) {
  public static final Codec<PolishingComponent> CODEC = RecordCodecBuilder.create(instance -> instance
      .group(
          Codec.INT.fieldOf("completed_stages").forGetter(PolishingComponent::completedStages),
          Codec.INT.fieldOf("ticks_in_stage").forGetter(PolishingComponent::ticksInStage),
          Codec.INT.fieldOf("stage_duration").forGetter(PolishingComponent::stageDuration))
      .apply(instance, PolishingComponent::new));

  public static final PacketCodec<RegistryByteBuf, PolishingComponent> PACKET_CODEC =
      PacketCodec.tuple(
          PacketCodecs.INTEGER, PolishingComponent::completedStages,
          PacketCodecs.INTEGER, PolishingComponent::ticksInStage,
          PacketCodecs.INTEGER, PolishingComponent::stageDuration,
          PolishingComponent::new);

  public PolishingComponent withTickProgress(int ticksInStage) {
    return new PolishingComponent(completedStages, ticksInStage, stageDuration);
  }

  public PolishingComponent nextStage(int nextStageDuration) {
    return new PolishingComponent(completedStages + 1, 0, nextStageDuration);
  }
}
