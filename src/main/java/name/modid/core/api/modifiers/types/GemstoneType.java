package name.modid.core.api.modifiers.types;

import com.mojang.serialization.Codec;

public enum GemstoneType {
  LOCKED("", "item.gemstones.locked_slot"),
  EMPTY("", "item.gemstones.empty_slot"),
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
  RESTLESS_FLAME("\uE012", "item.gemstones.gemstone_name.restless_flame"),
  AMBER("\uE013", "item.gemstones.gemstone_name.amber"),
  PYRITE("\uE014", "item.gemstones.gemstone_name.pyrite"),
  GARNET("\uE015", "item.gemstones.gemstone_name.garnet"),
  WITHER_SHELL("\uE016", "item.gemstones.gemstone_name.WITHER_SHELL"),
  POLYCHROME_CRYSTAL("\uE017", "item.gemstones.gemstone_name.polychrome_crystal"),
  ONYX("\uE018", "item.gemstones.gemstone_name.onyx"),
  ASTRALITE("\uE019", "item.gemstones.gemstone_name.astralite"),
  CRYSTALLIZED_EXPIRIENCE("\uE020", "item.gemstones.gemstone_name.crystallized_expirience"),
  ENDER_SCALE("\uE021", "item.gemstones.gemstone_name.ender_scale"),
  CHAOS_STONE("\uE022", "item.gemstones.gemstone_name.chaos_stone"),
  BLOODSTONE("\uE023", "item.gemstones.gemstone_name.bloodstone");

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
