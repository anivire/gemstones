package name.modid.core.api.modifiers.types;

import com.mojang.serialization.Codec;

import net.minecraft.util.Formatting;

public enum GemstoneQuality {
  NONE(-1, "\uE000", "item.gemstones.quality.none", Formatting.WHITE, 0xFFFFFF),
  CRUDE(0, "\uE004", "item.gemstones.quality.crude", Formatting.GRAY, 0x4d4d4d),
  POLISHED(1, "\uE004", "item.gemstones.quality.polished", Formatting.BLUE, 0x5454fc),
  FLAWLESS(2, "\uE004", "item.gemstones.quality.flawless", Formatting.LIGHT_PURPLE, 0xfc4dff),
  RADIANT(3, "\uE004", "item.gemstones.quality.radiant", Formatting.GOLD, 0xff8214),
  MYTHIC(4, "\uE005", "item.gemstones.quality.mythic", Formatting.DARK_PURPLE, 0x6000d6),
  AMPLIFIER(5, "\uE006", "item.gemstones.quality.amplifier", Formatting.RED, 0xe6254e);

  private final Integer value;
  private final String fontLiteral;
  private final String translationString;
  private final Formatting qualityColor;
  private final int qualityHexColor;
  public static final Codec<GemstoneQuality> CODEC = Codec.STRING.xmap(GemstoneQuality::fromSerializedName,
      GemstoneQuality::name);

  GemstoneQuality(Integer value, String fontLiteral, String translationString, Formatting qualityColor,
      int qualityHexColor) {
    this.value = value;
    this.fontLiteral = fontLiteral;
    this.translationString = translationString;
    this.qualityColor = qualityColor;
    this.qualityHexColor = qualityHexColor;
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

  public String getPathName() {
    return name().toLowerCase();
  }

  public Formatting getQualityTextcolor() {
    return qualityColor;
  }

  public int getQualityHexColor() {
    return qualityHexColor;
  }

  public GemstoneQuality next() {
    return switch (this) {
      case CRUDE -> POLISHED;
      case POLISHED -> FLAWLESS;
      case FLAWLESS -> RADIANT;
      case RADIANT -> MYTHIC;
      default -> null;
    };
  }

  public int getPolishStages() {
    return switch (this) {
      case CRUDE -> 3;
      case POLISHED -> 5;
      case FLAWLESS -> 7;
      default -> 0;
    };
  }

  public float getBreakChance() {
    return switch (this) {
      case CRUDE -> 0.1f;
      case POLISHED -> 0.15f;
      case FLAWLESS -> 0.2f;
      default -> 0f;
    };
  }

  private static GemstoneQuality fromSerializedName(String name) {
    try {
      return GemstoneQuality.valueOf(name);
    } catch (IllegalArgumentException e) {
      return NONE;
    }
  }
}
