package name.modid.core.api.components;

import java.util.Arrays;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GemstoneSlotsComponent(GemstoneComponent[] gemstones) {
  public static final Codec<GemstoneSlotsComponent> GEMSTONE_SLOTS_CODEC = RecordCodecBuilder.create(builder -> {
    return builder
        .group(
            GemstoneComponent.GEMSTONE_CODEC.listOf().fieldOf("slots").forGetter(item -> Arrays.asList(item.gemstones)))
        .apply(builder, list -> new GemstoneSlotsComponent(list.toArray(new GemstoneComponent[0])));
  });

  // Proper equals and hashCode implementations.
  // Default behavior cause visual glitches while item dragging
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    GemstoneSlotsComponent that = (GemstoneSlotsComponent) o;
    return Arrays.equals(gemstones, that.gemstones);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(gemstones);
  }
}
