package name.modid.fabric;

import name.modid.GemstonesClient;
import name.modid.core.api.OreHighlighter;
import name.modid.core.mixins.WorldRendererAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;

public class GemstonesFabricClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    GemstonesClient.initClient();
    registerOreHighlighterRenderHook();
  }

  private static void registerOreHighlighterRenderHook() {
    WorldRenderEvents.AFTER_ENTITIES.register(ctx -> {
      if (!OreHighlighter.hasActiveHighlights()) {
        return;
      }

      MinecraftClient client = MinecraftClient.getInstance();
      if (client.world == null || client.player == null) {
        return;
      }

      OreHighlighter.render(ctx.matrixStack(), ctx.camera(), ((WorldRendererAccessor) ctx.worldRenderer())
          .gemstones$getBufferBuilders()
          .getOutlineVertexConsumers());
    });
  }
}
