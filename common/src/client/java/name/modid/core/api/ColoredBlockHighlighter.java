package name.modid.core.api;

import java.util.Collection;
import java.util.EnumSet;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class ColoredBlockHighlighter {
  public record HighlightedBlock(BlockPos pos, int color) {
  }

  private static final ModelPart.Cuboid CUBE = new ModelPart.Cuboid(
      0, 0,
      0.0F, 0.0F, 0.0F,
      16.0F, 16.0F, 16.0F,
      0.0F, 0.0F, 0.0F,
      false,
      0.0F, 0.0F,
      EnumSet.allOf(Direction.class));
  private static final RenderLayer GLOW_LAYER = RenderLayer.getOutline(
      Identifier.ofVanilla("textures/block/stone.png"));

  private ColoredBlockHighlighter() {
  }

  public static void render(MatrixStack matrices,
      Camera camera,
      OutlineVertexConsumerProvider vertexConsumers,
      Collection<HighlightedBlock> highlights) {
    Vec3d cam = camera.getPos();

    matrices.push();
    matrices.translate(-cam.x, -cam.y, -cam.z);

    for (HighlightedBlock highlight : highlights) {
      renderHighlight(matrices, vertexConsumers, highlight.pos(), highlight.color());
    }

    matrices.pop();
  }

  private static void renderHighlight(MatrixStack matrices,
      OutlineVertexConsumerProvider vertexConsumers,
      BlockPos pos,
      int color) {
    VertexConsumer vertices = setOutlineColor(color, vertexConsumers);

    matrices.push();
    matrices.translate(pos.getX(), pos.getY(), pos.getZ());
    CUBE.renderCuboid(matrices.peek(), vertices, 0, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
    matrices.pop();
  }

  private static VertexConsumer setOutlineColor(int color,
      OutlineVertexConsumerProvider vertexConsumers) {
    vertexConsumers.setColor(
        color >> 16 & 0xFF,
        color >> 8 & 0xFF,
        color & 0xFF,
        0xFF);
    return vertexConsumers.getBuffer(GLOW_LAYER);
  }
}
