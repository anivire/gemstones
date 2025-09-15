package name.modid.core.api.modifiers.types;

import com.mojang.serialization.Codec;

public enum GemstoneQuality {
  NONE(-1, "\uE000", "item.gemstones.quality.none"),
  CRUDE(0, "\uE001", "item.gemstones.quality.crude"),
  POLISHED(1, "\uE002", "item.gemstones.quality.polished"),
  FLAWLESS(2, "\uE003", "item.gemstones.quality.flawless"),
  RADIANT(3, "\uE004", "item.gemstones.quality.radiant"),
  UNUSUAL(4, "\uE005", "item.gemstones.quality.unusual");

  private final Integer value;
  private final String fontLiteral;
  private final String translationString;
  public static final Codec<GemstoneQuality> CODEC = Codec.STRING.xmap(GemstoneQuality::valueOf, GemstoneQuality::name);

  GemstoneQuality(Integer value, String fontLiteral, String translationString) {
    this.value = value;
    this.fontLiteral = fontLiteral;
    this.translationString = translationString;
  }

  public Integer getValue() {
    return value;
  }

  public String getRarityLiteral() {
    return fontLiteral;
  }

  public String getTranslationString() {
    return translationString;
  }

}
