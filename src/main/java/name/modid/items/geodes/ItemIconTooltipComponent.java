package name.modid.items.geodes;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;

public class ItemIconTooltipComponent implements TooltipComponent {
  private final ItemStack stack;

  public ItemIconTooltipComponent(ItemStack stack) {
    this.stack = stack;
  }

  @Override
  public int getHeight() {
    return 16;
  }

  @Override
  public int getWidth(TextRenderer textRenderer) {
    return 16;
  }

  @Override
  public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
    context.drawItem(stack, x, y);
  }
}