package name.modid.core.api.modifiers.types;

import com.mojang.serialization.Codec;

public enum GemstoneType {
  LOCKED("\uE000", "item.gemstones.locked_slot"),
  EMPTY("\uE001", "item.gemstones.empty_slot"),
  RUBY("\uE002", "item.gemstones.gemstone_name.ruby"),
  CELESTINE("\uE003", "item.gemstones.gemstone_name.celestine"),
  SAPPHIRE("\uE004", "item.gemstones.gemstone_name.sapphire"),
  TOPAZ("\uE005", "item.gemstones.gemstone_name.topaz"),
  ZIRCON("\uE006", "item.gemstones.gemstone_name.zircon"),
  AQUAMARINE("\uE007", "item.gemstones.gemstone_name.aquamarine"),
  OBSIDIAN_SHARD("\uE008", "item.gemstones.gemstone_name.obsidian_shard"),
  OPAL("\uE009", "item.gemstones.gemstone_name.opal"),
  JADE("\uE010", "item.gemstones.gemstone_name.jade"),
  MALACHITE("\uE011", "item.gemstones.gemstone_name.malachite"),
  SPAWNER_CORE("\uE012", "item.gemstones.gemstone_name.spawner_core"),
  AMBER("\uE013", "item.gemstones.gemstone_name.amber"),
  GARNET("\uE015", "item.gemstones.gemstone_name.garnet"),
  PYRITE("\uE014", "item.gemstones.gemstone_name.pyrite");

  private final String translationString;
  private final String fontLiteral;
  public static final Codec<GemstoneType> CODEC = Codec.STRING.xmap(GemstoneType::valueOf, GemstoneType::name);

  GemstoneType(String fontLiteral, String translationString) {
    this.fontLiteral = fontLiteral;
    this.translationString = translationString;
  }

  public String getTranslationString() {
    return translationString;
  }

  public String getGemstoneLiteral() {
    return fontLiteral;
  }
}
