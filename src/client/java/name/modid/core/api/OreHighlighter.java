package name.modid.core.api;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.ColoredBlockHighlighter.HighlightedBlock;
import name.modid.core.mixins.WorldRendererAccessor;
import name.modid.core.network.OreVisionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class OreHighlighter {

  private static final List<OreVisionPayload.HighlightedOre> highlighted = new ArrayList<>();
  private static long lastUpdate = 0L;
  private static final long HIGHLIGHT_TIMEOUT_MS = 1000L;

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
    return hasActiveOreHighlights() || MultiMinerClientPreview.hasActiveHighlights();
  }

  private static boolean hasActiveOreHighlights() {
    return !highlighted.isEmpty()
        && System.currentTimeMillis() - lastUpdate <= HIGHLIGHT_TIMEOUT_MS;
  }

  private static void render(MatrixStack matrices,
      Camera camera,
      OutlineVertexConsumerProvider vertexConsumers) {
    List<HighlightedBlock> highlights = new ArrayList<>();

    if (hasActiveOreHighlights()) {
      for (OreVisionPayload.HighlightedOre ore : highlighted) {
        highlights.add(new HighlightedBlock(ore.pos(), ore.color()));
      }
    }

    highlights.addAll(MultiMinerClientPreview.getActiveHighlights());
    ColoredBlockHighlighter.render(matrices, camera, vertexConsumers, highlights);
  }
}
