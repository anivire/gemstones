package name.modid.core.api;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.ColoredBlockHighlighter.HighlightedBlock;
import name.modid.core.network.OreVisionPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class OreHighlighter {

  private static final List<OreVisionPayload.HighlightedOre> highlighted = new ArrayList<>();
  private static long lastUpdate = 0L;
  private static final long HIGHLIGHT_TIMEOUT_MS = 1000L;

  public static void registerNetworking() {
    NetworkManager.registerReceiver(NetworkManager.s2c(), OreVisionPayload.ID, OreVisionPayload.CODEC,
        (payload, context) -> context.queue(() -> {
        highlighted.clear();
        highlighted.addAll(payload.ores());
        lastUpdate = System.currentTimeMillis();
    }));
  }

  public static boolean hasActiveHighlights() {
    return hasActiveOreHighlights() || MultiMinerClientPreview.hasActiveHighlights();
  }

  private static boolean hasActiveOreHighlights() {
    return !highlighted.isEmpty()
        && System.currentTimeMillis() - lastUpdate <= HIGHLIGHT_TIMEOUT_MS;
  }

  public static void render(MatrixStack matrices,
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
