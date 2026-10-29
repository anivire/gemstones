package name.modid.core.api.entities.jeweleryTable;

import com.mojang.blaze3d.systems.RenderSystem;

import name.modid.Gemstones;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import name.modid.core.api.tooltips.TooltipText;
import name.modid.core.content.screen.JewelryTableScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class JewelryTableScreen extends HandledScreen<JewelryTableScreenHandler> {
  public static final Identifier GUI_TEXTURE = Identifier.of(Gemstones.MOD_ID,
      "textures/gui/jewelry_table/gui.png");
  private static final Identifier CONTAINER_BACKGROUND_TEXTURE = Identifier.of("minecraft",
      "textures/gui/container/crafting_table.png");
  private static final int PLATE_COLOR = 0x4F000000;
  private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/anvil/error");
  private static final int ERROR_X = 92;
  private static final int ERROR_Y = 32;
  private static final int AFFORDABLE_COLOR = 0x80FF20;
  private static final int TOO_EXPENSIVE_COLOR = 0xFF6060;

  public JewelryTableScreen(JewelryTableScreenHandler handler, PlayerInventory inventory, Text title) {
    super(handler, inventory, title);

    this.backgroundWidth = 176;
    this.backgroundHeight = 172;
    this.playerInventoryTitleY = this.backgroundHeight - 94;
  }

  @Override
  protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
    int x = (this.width - this.backgroundWidth) / 2;
    int y = (this.height - this.backgroundHeight) / 2;

    RenderSystem.setShaderTexture(0, GUI_TEXTURE);
    int topPartHeight = 82;
    context.drawTexture(GUI_TEXTURE, x, y, 0, 0, this.backgroundWidth, topPartHeight);

    RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND_TEXTURE);

    int bottomPartY = y + topPartHeight;
    int bottomPartHeight = 90;

    context.drawTexture(CONTAINER_BACKGROUND_TEXTURE, x, bottomPartY, 0, 76, this.backgroundWidth, bottomPartHeight);

    int cost = this.handler.getRemoveLevelCost();
    if (cost > 0 && !canAfford(cost)) {
      context.drawGuiTexture(ERROR_TEXTURE, x + ERROR_X, y + ERROR_Y, 28, 21);
    }
  }

  private boolean canAfford(int cost) {
    return this.client != null && this.client.player != null
        && (this.client.player.getAbilities().creativeMode || this.client.player.experienceLevel >= cost);
  }

  @Override
  protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
    context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
    context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX,
        this.playerInventoryTitleY, 4210752, false);
    drawLevelCost(context);
  }

  private void drawLevelCost(DrawContext context) {
    int cost = this.handler.getRemoveLevelCost();
    if (cost <= 0 || this.client == null || this.client.player == null) {
      return;
    }

    boolean affordable = canAfford(cost);
    Text text = Text.translatable("container.gemstones.jewelry_table.level_cost", Text.empty()
        .append(Text.literal(cost + " "))
        .append(TooltipText.icon(InlineIcons.EXPERIENCE)))
        .styled(style -> style.withColor(affordable ? AFFORDABLE_COLOR : TOO_EXPENSIVE_COLOR));

    int textY = this.playerInventoryTitleY - 3;
    int x = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;
    context.fill(x - 2, textY - 2, this.backgroundWidth - 8, textY + 10, PLATE_COLOR);
    context.drawTextWithShadow(this.textRenderer, text, x, textY, 0xFFFFFF);
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
    drawMouseoverTooltip(context, mouseX, mouseY);
  }
}