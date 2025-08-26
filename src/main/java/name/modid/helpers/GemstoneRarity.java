package name.modid.helpers;

import com.mojang.serialization.Codec;

public enum GemstoneRarity {
  NONE(-1), COMMON(0), UNCOMMON(1), RARE(2), LEGENDARY(3), UNUSUAL(4);

  private final int value;

  GemstoneRarity(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static final Codec<GemstoneRarity> CODEC = Codec.STRING.xmap(GemstoneRarity::valueOf, GemstoneRarity::name);

  public static String getRarityLiteral(GemstoneRarity type) {
    return switch (type) {
      case COMMON -> "\uE001";
      case UNCOMMON -> "\uE002";
      case RARE -> "\uE003";
      case LEGENDARY -> "\uE004";
      case UNUSUAL -> "\uE005";
      default -> "\uE000";
    };
  }
}
