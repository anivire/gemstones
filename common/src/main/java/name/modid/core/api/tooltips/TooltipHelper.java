package name.modid.core.api.tooltips;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.types.GemstoneType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

public class TooltipHelper {
  private static final Set<String> REPORTED_MISSING_KEYS = ConcurrentHashMap.newKeySet();

  public enum Icons {
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
    INFO_INDENT("\uE900"),
    HALF_HEART("\uE901"),
    BLEEDING("\uE902"),
    GUARDIAN_SMITE("\uE903"),
    HALF_EXTRA_HEART("\uE904"),
    TIDE("\uE905"),
    ARROW_UP("\uE906"),
    LIGHTNING_STRIKE("\uE907"),
    TORRENT("\uE908"),
    HARVEST_MARK("\uE909"),
    SLOWNESS("\uE90A"),
    STUNNED("\uE90B"),
    ARROW_DOWN("\uE90C"),
    LIFESTEAL("\uE90D"),
    MOUSE_RMB("\uE90E"),
    MOUSE_LMB("\uE90F"),
    SHIFT("\uE910"),
    LOCKED("\uE911"),
    EMPTY("\uE912"),
    INFO("\uE913");

    private final String symbol;

    InlineIcons(String symbol) {
      this.symbol = symbol;
    }

    public String getSymbol() {
      return symbol;
    }

    public MutableText asText() {
      return TooltipText.icon(this);
    }
  }

  public static Formatting getSlotColor(GemstoneType gemType) {
    return switch (gemType) {
      case EMPTY -> Formatting.DARK_GRAY;
      case LOCKED -> Formatting.RED;
      case AMBER -> Formatting.RED;
      default -> Formatting.WHITE;
    };
  }

  public static String getSlotText(GemstoneType gemType) {
    return switch (gemType) {
      case LOCKED -> "tooltip.gemstones.gemstone_type.locked";
      case EMPTY -> "tooltip.gemstones.gemstone_type.empty";
      case UNDEFINED -> "tooltip.gemstones.gemstone_type.undefined";
      default -> "tooltip.gemstones.gemstone_type.undefined";
    };
  }

  public static MutableText safeTranslatable(String key, Object... args) {
    try {
      MutableText text = Text.translatable(key, args);
      if (Language.getInstance() != null && !Language.getInstance().hasTranslation(key)) {
        // Tooltips are rebuilt every frame, report each key only once
        if (REPORTED_MISSING_KEYS.add(key)) {
          Gemstones.LOGGER.warn("Missing lang key: {}", key);
        }
        return Text.literal("Missing lang key: " + key).formatted(Formatting.RED);
      }
      return text;
    } catch (Throwable t) {
      Gemstones.LOGGER.error("Error while translating key {}", key, t);
      return Text.literal(key).formatted(Formatting.RED);
    }
  }

  public static MutableText buildTextWithIcon(InlineIcons icon, String text) {
    return TooltipText.line(
        TooltipText.icon(icon),
        Text.literal(" " + text).formatted(Formatting.RED));
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
    return Text.literal(builder.formatValue(seconds, Text.translatable("tooltip.gemstones.unit.second").getString()))
        .styled(s -> s.withFont(Style.DEFAULT_FONT_ID))
        .formatted(actualColor);
  }

  public static MutableText buildBlocksText(
      TooltipBuilder builder,
      double blocks,
      @Nullable Formatting color) {
    Formatting actualColor = color != null ? color : Formatting.GREEN;
    return Text.literal(builder.formatValue(blocks, Text.translatable("tooltip.gemstones.unit.block").getString()))
        .styled(s -> s.withFont(Style.DEFAULT_FONT_ID))
        .formatted(actualColor);
  }

  public static String formatHealth(TooltipBuilder builder, double health) {
    return builder.formatValue(health, Text.translatable("tooltip.gemstones.unit.health").getString());
  }
}
