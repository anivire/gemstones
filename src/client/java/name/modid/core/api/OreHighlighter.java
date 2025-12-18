package name.modid.core.api;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import name.modid.core.network.OreVisionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class OreHighlighter {

  private static final List<BlockPos> highlighted = new ArrayList<>();
  private static long lastUpdate = 0L;

  private static final RenderLayer XRAY_LAYER = RenderLayer.of(
      "ore_outline",
      VertexFormats.LINES,
      VertexFormat.DrawMode.LINES,
      256,
      false,
      true,
      RenderLayer.MultiPhaseParameters.builder()
          .program(RenderPhase.LINES_PROGRAM)
          .lineWidth(RenderPhase.FULL_LINE_WIDTH)
          .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
          .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
          .writeMaskState(RenderPhase.ALL_MASK)
          .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
          .cull(RenderPhase.DISABLE_CULLING)
          .build(false));

  public static void register() {
    ClientPlayNetworking.registerGlobalReceiver(OreVisionPayload.ID, (payload, context) -> {
      MinecraftClient client = MinecraftClient.getInstance();
      client.execute(() -> {
        highlighted.clear();
        highlighted.addAll(payload.ores());
        lastUpdate = System.currentTimeMillis();
      });
    });

    WorldRenderEvents.AFTER_TRANSLUCENT.register(ctx -> {
      if (System.currentTimeMillis() - lastUpdate > 1000L) {
        return;
      }

      MinecraftClient client = MinecraftClient.getInstance();
      if (client.world == null || client.player == null) {
        return;
      }

      var consumers = ctx.consumers();
      if (consumers == null) {
        return;
      }

      MatrixStack matrices = ctx.matrixStack();
      Vec3d cam = ctx.camera().getPos();

      VertexConsumer outline = consumers.getBuffer(XRAY_LAYER);
      VertexConsumer fill = consumers.getBuffer(XRAY_LAYER);

      matrices.push();
      matrices.translate(-cam.x, -cam.y, -cam.z);

      for (Box box : mergeClusters(highlighted)) {
        drawOutline(matrices, outline, fill, box);
        spawnParticles(client, box);
      }

      matrices.pop();
    });
  }

  private static List<Box> mergeClusters(List<BlockPos> list) {
    Set<BlockPos> all = new HashSet<>(list);
    List<Box> result = new ArrayList<>();

    while (!all.isEmpty()) {
      BlockPos start = all.iterator().next();
      all.remove(start);

      Set<BlockPos> cluster = new HashSet<>();
      Deque<BlockPos> q = new ArrayDeque<>();
      q.add(start);

      while (!q.isEmpty()) {
        BlockPos cur = q.poll();
        cluster.add(cur);
        for (Direction d : Direction.values()) {
          BlockPos next = cur.offset(d);
          if (all.remove(next)) {
            q.add(next);
          }
        }
      }

      int minX = cluster.stream().mapToInt(BlockPos::getX).min().orElse(start.getX());
      int minY = cluster.stream().mapToInt(BlockPos::getY).min().orElse(start.getY());
      int minZ = cluster.stream().mapToInt(BlockPos::getZ).min().orElse(start.getZ());
      int maxX = cluster.stream().mapToInt(BlockPos::getX).max().orElse(start.getX());
      int maxY = cluster.stream().mapToInt(BlockPos::getY).max().orElse(start.getY());
      int maxZ = cluster.stream().mapToInt(BlockPos::getZ).max().orElse(start.getZ());

      result.add(new Box(
          minX, minY, minZ,
          maxX + 1.0, maxY + 1.0, maxZ + 1.0));
    }

    return result;
  }

  private static void drawOutline(
      MatrixStack matrices,
      VertexConsumer outline,
      VertexConsumer fill,
      Box box) {
    float r = 1f, g = 1f, b = 1f, a = 0.95f;

    WorldRenderer.drawBox(matrices, outline,
        box.minX, box.minY, box.minZ,
        box.maxX, box.maxY, box.maxZ,
        r, g, b, a, r, g, b);
  }

  private static void spawnParticles(MinecraftClient client, Box box) {
    if (client.world == null) {
      return;
    }

    var world = client.world;
    var rand = world.random;

    if (rand.nextFloat() > 0.1f) {
      return;
    }

    double padding = 0.15;
    double xLen = (box.maxX - box.minX) + padding * 2;
    double yLen = (box.maxY - box.minY) + padding * 2;
    double zLen = (box.maxZ - box.minZ) + padding * 2;
    double baseX = box.minX - padding;
    double baseY = box.minY - padding;
    double baseZ = box.minZ - padding;

    double px = baseX + rand.nextDouble() * xLen;
    double py = baseY + rand.nextDouble() * yLen;
    double pz = baseZ + rand.nextDouble() * zLen;

    world.addParticle(ParticleTypes.END_ROD, px, py, pz, 0, 0.002, 0);
  }
}
