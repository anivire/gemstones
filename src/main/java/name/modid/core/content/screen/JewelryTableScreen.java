package name.modid.core.content.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import name.modid.Gemstones;
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
  }

  @Override
  protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
    context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
    context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX,
        this.playerInventoryTitleY, 4210752, false);
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
    drawMouseoverTooltip(context, mouseX, mouseY);
  }
}