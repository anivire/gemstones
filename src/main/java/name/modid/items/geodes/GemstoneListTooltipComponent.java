package name.modid.items.geodes;

import java.util.List;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class GemstoneListTooltipComponent implements TooltipComponent {
  private final List<ItemStack> stacks;

  public GemstoneListTooltipComponent(List<ItemStack> stacks) {
    this.stacks = stacks;
  }

  @Override
  public int getHeight() {
    // Высота = 18 пикселей на строку (иконка + текст)
    return stacks.size() * 18;
  }

  @Override
  public int getWidth(TextRenderer textRenderer) {
    // Ширина = иконка (16) + отступ + ширина текста
    int maxWidth = 0;
    for (ItemStack stack : stacks) {
      int textWidth = textRenderer.getWidth(stack.getName());
      maxWidth = Math.max(maxWidth, 20 + textWidth);
    }
    return maxWidth;
  }

  @Override
  public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
    int offsetY = 0;
    for (ItemStack stack : stacks) {
      // Рисуем иконку
      context.drawItem(stack, x, y + offsetY);

      // Рисуем текст рядом
      context.drawText(textRenderer, Text.literal("∙ ").append(stack.getName()),
          x + 20, y + offsetY + 4, 0xFFFFFF, false);

      offsetY += 18;
    }
  }
}