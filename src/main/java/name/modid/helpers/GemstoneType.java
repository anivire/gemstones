package name.modid.helpers;

import com.mojang.serialization.Codec;

public enum GemstoneType {
  EMPTY,
  LOCKED,
  RUBY,
  CELESTINE,
  TOPAZ,
  SAPPHIRE,
  ZIRCON,
  AQUAMARINE,
  OBSIDIAN_SHARD,
  OPAL,
  JADE,
  MALACHITE,
  SPAWNER_CORE,
  AMBER,
  GARNET,
  PYRITE;

  public static final Codec<GemstoneType> CODEC = Codec.STRING.xmap(GemstoneType::valueOf, GemstoneType::name);

  public static String getGemstoneLiteral(GemstoneType type) {
    return switch (type) {
      case EMPTY -> "\uE001";
      case RUBY -> "\uE002";
      case CELESTINE -> "\uE003";
      case SAPPHIRE -> "\uE004";
      case TOPAZ -> "\uE005";
      case ZIRCON -> "\uE006";
      case AQUAMARINE -> "\uE007";
      case OBSIDIAN_SHARD -> "\uE008";
      case OPAL -> "\uE009";
      case JADE -> "\uE010";
      case MALACHITE -> "\uE011";
      case SPAWNER_CORE -> "\uE012";
      case AMBER -> "\uE013";
      case PYRITE -> "\uE014";
      case GARNET -> "\uE015";
      default -> "\uE000";
    };
  }
}
