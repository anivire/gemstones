package name.modid.core.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.joml.Matrix4f;

import name.modid.core.api.OreHighlighter;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
  @Shadow
  @Final
  private BufferBuilderStorage bufferBuilders;

  // Rendering approach inspired by Leximon's Spelunker mod:
  // https://github.com/Leximon/Spelunker
  @ModifyVariable(
      method = "render",
      at = @At(value = "CONSTANT", args = "stringValue=blockentities", ordinal = 0),
      ordinal = 3)
  private boolean gemstones$renderOreVisionOutlinePostProcess(boolean value) {
    return value || OreHighlighter.hasActiveHighlights();
  }

  @Inject(
      method = "render",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V",
          shift = At.Shift.BEFORE))
  private void gemstones$renderOreVisionHighlights(RenderTickCounter tickCounter,
      boolean renderBlockOutline,
      Camera camera,
      GameRenderer gameRenderer,
      LightmapTextureManager lightmapTextureManager,
      Matrix4f positionMatrix,
      Matrix4f projectionMatrix,
      CallbackInfo ci,
      @Local MatrixStack matrices) {
    if (!OreHighlighter.hasActiveHighlights()) {
      return;
    }

    OutlineVertexConsumerProvider outlines = bufferBuilders.getOutlineVertexConsumers();
    OreHighlighter.render(matrices, camera, outlines);
  }
}
