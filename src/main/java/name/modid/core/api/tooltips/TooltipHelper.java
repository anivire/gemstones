package name.modid.core.api.tooltips;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import name.modid.Gemstones;
import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

public class TooltipHelper {

  public enum Icons {
    SOCKET("gemstone_sockets_font"),
    INLINE_GEMSTONE("gemstone_inline_font"),
    INLINE("icons_font"),
    QUALITY("quality_font");

    private final String path;

    Icons(String path) {
      this.path = path;
    }

    public String getPath() {
      return path;
    }
  }

  public enum InlineIcons {
    HALF_HEART("\uE001"),
    BLEEDING("\uE002"),
    GUARDIAN_SMITE("\uE003"),
    HALF_EXTRA_HEART("\uE004"),
    TIDE("\uE005"),
    ARROW_UP("\uE006"),
    LIGHTNING_STRIKE("\uE007"),
    TORRENT("\uE008"),
    HARVEST_MARK("\uE009"),
    SLOWNESS("\uE010"),
    STUNNED("\uE011"),
    ARROW_DOWN("\uE012"),
    LIFESTEAL("\uE013"),
    MOUSE_RMB("\uE014"),
    MOUSE_LMB("\uE015"),
    SHIFT("\uE016"),
    LOCKED("\uE017"),
    EMPTY("\uE018"),
    INFO("\uE019");

    private final String symbol;

    InlineIcons(String symbol) {
      this.symbol = symbol;
    }

    public String getSymbol() {
      return symbol;
    }

    public MutableText asText() {
      return Text.literal(symbol).styled(s -> s.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())));
    }
  }

  public static Formatting getSlotColor(GemstoneType gemType) {
    return switch (gemType) {
      case EMPTY -> Formatting.DARK_GRAY;
      case LOCKED -> Formatting.RED;
      default -> Formatting.WHITE;
    };
  }

  public static String getSlotText(GemstoneType gemType) {
    return switch (gemType) {
      case LOCKED -> "tooltip.gemstones.gemstone_type.locked";
      case EMPTY -> "tooltip.gemstones.gemstone_type.empty";
      default -> "tooltip.gemstones.gemstone_type.undefined";
    };
  }

  private static Text getGemstoneSprite(GemstoneType gemstoneType) {
    return Text.literal(gemstoneType.getGemstoneLiteral())
        .styled(s -> s.withFont(Identifier.of(Gemstones.MOD_ID, Icons.SOCKET.getPath())));
  }

  public static MutableText getGemstoneQualitySprite(GemstoneQuality rarityType) {
    return Text.literal(rarityType.getRarityLiteral())
        .styled(s -> s.withFont(Identifier.of(Gemstones.MOD_ID, Icons.QUALITY.getPath()))).formatted(Formatting.WHITE);
  }

  public static Text getGemstoneSocketedRow(GemstoneComponent[] gemstones) {
    MutableText row = Text.empty();
    for (GemstoneComponent slot : gemstones) {
      row.append(getGemstoneSprite(slot.gemstoneType()));
    }
    return row;
  }

  public static MutableText safeTranslatable(String key, Object... args) {
    try {
      MutableText text = Text.translatable(key, args);
      if (Language.getInstance() != null && !Language.getInstance().hasTranslation(key)) {
        Gemstones.LOGGER.warn("Missing lang key: {}", key);
        return Text.literal("Missing lang key: " + key).formatted(Formatting.RED);
      }
      return text;
    } catch (Throwable t) {
      Gemstones.LOGGER.error("Error while translating key {}", key, t);
      return Text.literal(key).formatted(Formatting.RED);
    }
  }

  public static MutableText makeRow(String icon, Icons fontIcon, Text content, Optional<Boolean> isGemstoneIcon) {
    return makeRow(icon, fontIcon.getPath(), content, isGemstoneIcon);
  }

  public static MutableText makeRow(String icon, String fontPath, Text content, Optional<Boolean> isGemstoneIcon) {
    Formatting iconColor = isGemstoneIcon.orElse(false) ? Formatting.WHITE : Formatting.DARK_GRAY;

    MutableText prefix = Text.literal(icon).setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, fontPath)))
        .formatted(iconColor)
        .append(Text.literal(" > ").formatted(Formatting.DARK_GRAY).styled(s -> s.withFont(Style.DEFAULT_FONT_ID)));

    return prefix.append(content);
  }

  public static MutableText buildTextWithIcon(InlineIcons icon, String text) {
    MutableText i = Text.literal(icon.getSymbol())
        .styled(s -> s.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);

    // TODO: translatable text
    MutableText t = Text.literal(" " + text)
        .styled(s -> s.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.RED);

    return Text.empty()
        // .styled(s -> s.withFont(Style.DEFAULT_FONT_ID))
        .append(i)
        .append(t);
  }

  public static MutableText buildChanceText(
      TooltipBuilder builder,
      double chance,
      boolean isPositive,
      @Nullable Formatting color) {
    Formatting actualColor = color != null ? color : Formatting.GREEN;
    String chanceText = builder.formatValue(chance * 100, "%");

    return Text.empty()
        .append(builder.getArrowPrefix(isPositive).copy())
        .append(Text.literal(chanceText).formatted(actualColor))
        .styled(s -> s.withFont(Style.DEFAULT_FONT_ID));
  }

  public static MutableText buildSecondsText(
      TooltipBuilder builder,
      double seconds,
      @Nullable Formatting color) {
    Formatting actualColor = color != null ? color : Formatting.GREEN;
    return Text.literal(builder.formatValue(seconds, " second"))
        .styled(s -> s.withFont(Style.DEFAULT_FONT_ID))
        .formatted(actualColor);
  }

  public static MutableText buildBlocksText(
      TooltipBuilder builder,
      double blocks,
      @Nullable Formatting color) {
    Formatting actualColor = color != null ? color : Formatting.GREEN;
    return Text.literal(builder.formatValue(blocks, " block"))
        .styled(s -> s.withFont(Style.DEFAULT_FONT_ID))
        .formatted(actualColor);
  }
}