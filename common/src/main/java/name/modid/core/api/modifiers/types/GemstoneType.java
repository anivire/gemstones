package name.modid.core.api.modifiers.types;

import com.mojang.serialization.Codec;

public enum GemstoneType {
  LOCKED("", "item.gemstones.locked_slot"),
  EMPTY("", "item.gemstones.empty_slot"),
  UNDEFINED("", "tooltip.gemstones.gemstone_type.undefined"),
  RUBY("\uE940", "item.gemstones.gemstone_name.ruby"),
  CELESTINE("\uE941", "item.gemstones.gemstone_name.celestine"),
  SAPPHIRE("\uE942", "item.gemstones.gemstone_name.sapphire"),
  TOPAZ("\uE943", "item.gemstones.gemstone_name.topaz"),
  ZIRCON("\uE944", "item.gemstones.gemstone_name.zircon"),
  AQUAMARINE("\uE945", "item.gemstones.gemstone_name.aquamarine"),
  OBSIDIAN_SHARD("\uE946", "item.gemstones.gemstone_name.obsidian_shard"),
  OPAL("\uE947", "item.gemstones.gemstone_name.opal"),
  JADE("\uE948", "item.gemstones.gemstone_name.jade"),
  MALACHITE("\uE949", "item.gemstones.gemstone_name.malachite"),
  RESTLESS_FLAME("\uE94A", "item.gemstones.gemstone_name.restless_flame"),
  AMBER("\uE94B", "item.gemstones.gemstone_name.amber"),
  PYRITE("\uE94C", "item.gemstones.gemstone_name.pyrite"),
  GARNET("\uE94D", "item.gemstones.gemstone_name.garnet"),
  WITHER_SHELL("\uE94E", "item.gemstones.gemstone_name.wither_shell"),
  POLYCHROME_CRYSTAL("\uE94F", "item.gemstones.gemstone_name.polychrome_crystal"),
  ONYX("\uE950", "item.gemstones.gemstone_name.onyx"),
  ASTRALITE("\uE951", "item.gemstones.gemstone_name.astralite"),
  CRYSTALLIZED_EXPERIENCE("\uE952", "item.gemstones.gemstone_name.crystallized_experience"),
  ENDER_SCALE("\uE953", "item.gemstones.gemstone_name.ender_scale"),
  CHAOS_STONE("\uE954", "item.gemstones.gemstone_name.chaos_stone"),
  BLOODSTONE("\uE955", "item.gemstones.gemstone_name.bloodstone");

  private final String translationString;
  private final String fontLiteral;
  public static final Codec<GemstoneType> CODEC = Codec.STRING.xmap(
      GemstoneType::fromSerializedName,
      GemstoneType::name);

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

  private static GemstoneType fromSerializedName(String name) {
    try {
      return GemstoneType.valueOf(name);
    } catch (IllegalArgumentException e) {
      return UNDEFINED;
    }
  }
}
