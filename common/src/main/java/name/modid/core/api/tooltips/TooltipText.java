package name.modid.core.api.tooltips;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.tooltips.TooltipHelper.Icons;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * Builders for tooltip lines that mix icon glyphs with regular text.
 *
 * Every line is rooted in an empty default-font text and icons are appended as
 * siblings, never as parents of the text. The icon font therefore never leaks
 * into the rest of the line, even when another mod re-applies the root style
 * of a line to all of its contents
 */
public final class TooltipText {
  private static final Style DEFAULT_FONT = Style.EMPTY.withFont(Style.DEFAULT_FONT_ID);

  private TooltipText() {
  }

  public static Identifier font(Icons font) {
    return Identifier.of(Gemstones.MOD_ID, font.getPath());
  }

  public static MutableText icon(String symbol, Icons font, Formatting color) {
    return Text.literal(symbol).setStyle(Style.EMPTY.withFont(font(font)).withFormatting(color));
  }

  public static MutableText icon(InlineIcons icon) {
    return icon(icon, Formatting.WHITE);
  }

  public static MutableText icon(InlineIcons icon, Formatting color) {
    return icon(icon.getSymbol(), Icons.INLINE, color);
  }

  public static MutableText gemstoneIcon(GemstoneType type) {
    return icon(type.getGemstoneLiteral(), Icons.INLINE_GEMSTONE, Formatting.WHITE);
  }

  /** Gemstone sprite, or a marker for empty, locked and unknown sockets */
  public static MutableText socketIcon(GemstoneType type) {
    return switch (type) {
      case EMPTY -> icon(InlineIcons.EMPTY, Formatting.DARK_GRAY);
      case LOCKED -> icon(InlineIcons.LOCKED, Formatting.DARK_GRAY);
      case UNDEFINED -> icon(InlineIcons.INFO);
      default -> gemstoneIcon(type);
    };
  }

  /** A line of independent parts: none of them inherits the style of another */
  public static MutableText line(Text... parts) {
    MutableText line = Text.empty().setStyle(DEFAULT_FONT);
    for (Text part : parts) {
      line.append(part);
    }
    return line;
  }

  /** {@code icon > content} row used by socket and modifier listing */
  public static MutableText row(Text icon, Text content) {
    return line(icon, Text.literal(" > ").formatted(Formatting.DARK_GRAY), content);
  }

  /** Line prefixed with the info icon */
  public static MutableText info(Text content) {
    return line(icon(InlineIcons.INFO), content);
  }

  /** Line aligned with the text of an {@link #info(Text)} line */
  public static MutableText indented(Text content) {
    return line(icon(InlineIcons.INFO_INDENT), content);
  }
}
