package name.modid.core.api.tooltips;

import java.util.List;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipHelper.Icons;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class QualityTooltipComponent implements TooltipComponent {
  private static final Identifier FRAME_DEFAULT = Identifier.of(Gemstones.MOD_ID,
      "textures/gui/quality/frame_default.png");
  private static final Identifier FRAME_UNIQUE = Identifier.of(Gemstones.MOD_ID,
      "textures/gui/quality/frame_unique.png");

  private static final int HEIGHT = 10;
  private static final int ICON_WIDTH = 7;
  private static final int LEFT_WIDTH = 4;
  private static final int MIDDLE_WIDTH = 1;
  private static final int DEFAULT_RIGHT_WIDTH = 3;
  private static final int UNIQUE_RIGHT_WIDTH = 4;
  private static final int ICON_TEXT_GAP = 3;
  private static final int TEXT_RIGHT_PADDING = 3;
  private static final int BADGE_GAP = 1;

  private final List<GemstoneQuality> qualities;

  public QualityTooltipComponent(List<GemstoneQuality> qualities) {
    this.qualities = qualities;
  }

  @Override
  public int getHeight() {
    return HEIGHT;
  }

  @Override
  public int getWidth(TextRenderer textRenderer) {
    int width = 0;
    for (GemstoneQuality quality : qualities) {
      if (width > 0) {
        width += BADGE_GAP;
      }
      width += getBadgeWidth(textRenderer, quality);
    }
    return width;
  }

  @Override
  public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
    int nextX = x;
    for (GemstoneQuality quality : qualities) {
      drawBadge(textRenderer, context, quality, nextX, y);
      nextX += getBadgeWidth(textRenderer, quality) + BADGE_GAP;
    }
  }

  private void drawBadge(TextRenderer textRenderer, DrawContext context, GemstoneQuality quality, int x, int y) {
    Identifier frame = getFrame(quality);
    int rightWidth = getRightWidth(quality);
    Text label = Text.translatable(quality.getTranslationString()).formatted(Formatting.WHITE);
    int textWidth = textRenderer.getWidth(label);
    int contentWidth = ICON_WIDTH + ICON_TEXT_GAP + textWidth + TEXT_RIGHT_PADDING;
    int totalWidth = LEFT_WIDTH + contentWidth + rightWidth;
    int frameColor = getFrameColor(quality);

    context.setShaderColor(
        ((frameColor >> 16) & 0xFF) / 255.0f,
        ((frameColor >> 8) & 0xFF) / 255.0f,
        (frameColor & 0xFF) / 255.0f,
        1.0f);
    context.drawTexture(frame, x, y, LEFT_WIDTH, HEIGHT, 0.0f, 0.0f, LEFT_WIDTH, HEIGHT, getFrameTextureWidth(quality),
        HEIGHT);

    for (int offset = 0; offset < contentWidth; offset++) {
      context.drawTexture(frame, x + LEFT_WIDTH + offset, y, MIDDLE_WIDTH, HEIGHT, LEFT_WIDTH, 0.0f, MIDDLE_WIDTH,
          HEIGHT, getFrameTextureWidth(quality), HEIGHT);
    }

    context.drawTexture(frame, x + totalWidth - rightWidth, y, rightWidth, HEIGHT, LEFT_WIDTH + MIDDLE_WIDTH, 0.0f,
        rightWidth, HEIGHT, getFrameTextureWidth(quality), HEIGHT);
    context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

    int iconX = x + LEFT_WIDTH + 1;
    int iconY = y + 1;
    context.drawText(textRenderer, getIconText(quality), iconX, iconY, 0xFFFFFFFF, false);
    context.drawText(textRenderer, label, iconX + ICON_WIDTH + ICON_TEXT_GAP, y + 1, 0xFFFFFFFF, false);
  }

  private int getBadgeWidth(TextRenderer textRenderer, GemstoneQuality quality) {
    int textWidth = textRenderer.getWidth(Text.translatable(quality.getTranslationString()));
    return LEFT_WIDTH + ICON_WIDTH + ICON_TEXT_GAP + textWidth + TEXT_RIGHT_PADDING + getRightWidth(quality);
  }

  private static Identifier getFrame(GemstoneQuality quality) {
    return isUnique(quality) ? FRAME_UNIQUE : FRAME_DEFAULT;
  }

  private static int getRightWidth(GemstoneQuality quality) {
    return isUnique(quality) ? UNIQUE_RIGHT_WIDTH : DEFAULT_RIGHT_WIDTH;
  }

  private static int getFrameTextureWidth(GemstoneQuality quality) {
    return LEFT_WIDTH + MIDDLE_WIDTH + getRightWidth(quality);
  }

  private static boolean isUnique(GemstoneQuality quality) {
    return quality == GemstoneQuality.MYTHIC || quality == GemstoneQuality.AMPLIFIER;
  }

  private static int getFrameColor(GemstoneQuality quality) {
    return quality.getQualityHexColor();
  }

  private static MutableText getIconText(GemstoneQuality quality) {
    return Text.literal(quality.getRarityLiteral())
        .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.QUALITY.getPath())))
        .formatted(Formatting.WHITE);
  }
}
