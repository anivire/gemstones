package name.modid.core.api.modifiers.types;

import com.mojang.serialization.Codec;

import net.minecraft.util.Formatting;

public enum GemstoneQuality {
  NONE(-1, "\uE000", "item.gemstones.quality.none", Formatting.WHITE),
  CRUDE(0, "\uE001", "item.gemstones.quality.crude", Formatting.GRAY),
  POLISHED(1, "\uE002", "item.gemstones.quality.polished", Formatting.BLUE),
  FLAWLESS(2, "\uE003", "item.gemstones.quality.flawless", Formatting.LIGHT_PURPLE),
  RADIANT(3, "\uE004", "item.gemstones.quality.radiant", Formatting.GOLD),
  UNUSUAL(4, "\uE005", "item.gemstones.quality.unusual", Formatting.GREEN);

  private final Integer value;
  private final String fontLiteral;
  private final String translationString;
  private final Formatting qualityColor;
  public static final Codec<GemstoneQuality> CODEC = Codec.STRING.xmap(GemstoneQuality::fromSerializedName,
      GemstoneQuality::name);

  GemstoneQuality(Integer value, String fontLiteral, String translationString, Formatting qualityColor) {
    this.value = value;
    this.fontLiteral = fontLiteral;
    this.translationString = translationString;
    this.qualityColor = qualityColor;
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

  public Formatting getQualityTextcolor() {
    return qualityColor;
  }

  private static GemstoneQuality fromSerializedName(String name) {
    try {
      return GemstoneQuality.valueOf(name);
    } catch (IllegalArgumentException e) {
      return NONE;
    }
  }
}
