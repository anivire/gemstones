package name.modid.core.api.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;

public record GemstoneComponent(GemstoneType gemstoneType, GemstoneQuality gemstoneQualityType) {
  public static final Codec<GemstoneComponent> GEMSTONE_CODEC = RecordCodecBuilder.create(builder -> {
    return builder.group(
        GemstoneType.CODEC.fieldOf("gemstoneType").forGetter(GemstoneComponent::gemstoneType),
        GemstoneQuality.CODEC.fieldOf("qualityType").forGetter(GemstoneComponent::gemstoneQualityType))
        .apply(builder, GemstoneComponent::new);
  });
}
