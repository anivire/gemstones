package name.modid.core.api;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import name.modid.core.mixins.WorldRendererAccessor;
import name.modid.core.network.OreVisionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
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

public class OreHighlighter {

  private static final List<OreVisionPayload.HighlightedOre> highlighted = new ArrayList<>();
  private static long lastUpdate = 0L;
  private static final long HIGHLIGHT_TIMEOUT_MS = 1000L;

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

  public static void register() {
    ClientPlayNetworking.registerGlobalReceiver(OreVisionPayload.ID, (payload, context) -> {
      MinecraftClient client = MinecraftClient.getInstance();
      client.execute(() -> {
        highlighted.clear();
        highlighted.addAll(payload.ores());
        lastUpdate = System.currentTimeMillis();
      });
    });

    WorldRenderEvents.AFTER_ENTITIES.register(ctx -> {
      if (!hasActiveHighlights()) {
        return;
      }

      MinecraftClient client = MinecraftClient.getInstance();
      if (client.world == null || client.player == null) {
        return;
      }

      render(ctx.matrixStack(), ctx.camera(), ((WorldRendererAccessor) ctx.worldRenderer())
          .gemstones$getBufferBuilders()
          .getOutlineVertexConsumers());
    });
  }

  public static boolean hasActiveHighlights() {
    return !highlighted.isEmpty()
        && System.currentTimeMillis() - lastUpdate <= HIGHLIGHT_TIMEOUT_MS;
  }

  private static void render(MatrixStack matrices,
      Camera camera,
      OutlineVertexConsumerProvider vertexConsumers) {
    Vec3d cam = camera.getPos();

    matrices.push();
    matrices.translate(-cam.x, -cam.y, -cam.z);

    for (OreVisionPayload.HighlightedOre ore : highlighted) {
      renderOre(matrices, vertexConsumers, ore.pos(), ore.color());
    }

    matrices.pop();
  }

  private static void renderOre(MatrixStack matrices,
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
