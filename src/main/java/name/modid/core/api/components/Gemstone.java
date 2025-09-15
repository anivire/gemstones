package name.modid.core.api.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;

public record Gemstone(GemstoneType gemstoneType, GemstoneQuality GemstoneQualityType) {
  public static final Codec<Gemstone> GEMSTONE_CODEC = RecordCodecBuilder.create(builder -> {
    return builder.group(
        GemstoneType.CODEC.fieldOf("gemstoneType").forGetter(Gemstone::gemstoneType),
        GemstoneQuality.CODEC.fieldOf("GemstoneQualityType").forGetter(Gemstone::GemstoneQualityType))
        .apply(builder, Gemstone::new);
  });
}
