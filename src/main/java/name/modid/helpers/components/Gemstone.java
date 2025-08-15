package name.modid.helpers.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;

public record Gemstone(GemstoneType gemstoneType, GemstoneRarity gemstoneRarityType) {
  public static final Codec<Gemstone> GEMSTONE_CODEC = RecordCodecBuilder.create(builder -> {
    return builder.group(
        GemstoneType.CODEC.fieldOf("gemstoneType").forGetter(Gemstone::gemstoneType),
        GemstoneRarity.CODEC.fieldOf("gemstoneRarityType").forGetter(Gemstone::gemstoneRarityType))
        .apply(builder, Gemstone::new);
  });
}
