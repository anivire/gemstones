package name.modid.helpers.types;

import com.mojang.serialization.Codec;

public enum GemstoneType {
  EMPTY, LOCKED, RUBY, CELESTINE, TOPAZ, SAPPHIRE, ZIRCON, AQUAMARINE, OBSIDIAN_SHARD, OPAL;

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
      default -> "\uE000";
    };
  }
}
