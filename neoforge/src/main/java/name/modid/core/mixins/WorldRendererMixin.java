package name.modid.core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.OreHighlighter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;

// Rendering approach inspired by Leximon's Spelunker mod:
// https://github.com/Leximon/Spelunker
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
  @Shadow
  @Final
  private MinecraftClient client;

  @Shadow
  private PostEffectProcessor entityOutlinePostProcessor;

  @Unique
  private boolean gemstones$outlinesProcessed;

  @Shadow
  protected abstract boolean canDrawEntityOutlines();

  @Inject(method = "render", at = @At("HEAD"))
  private void gemstones$resetOutlineState(CallbackInfo ci) {
    gemstones$outlinesProcessed = false;
  }

  // Vanilla runs the outline pass only when a glowing entity is on screen, so with ore highlights
  // it is run here, without touching the vanilla locals
  @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V"))
  private void gemstones$renderOreVisionHighlights(OutlineVertexConsumerProvider outlines,
      Operation<Void> original,
      @Local(argsOnly = true) RenderTickCounter tickCounter,
      @Local(argsOnly = true) Camera camera,
      @Local MatrixStack matrices) {
    boolean highlight = OreHighlighter.hasActiveHighlights() && canDrawEntityOutlines();
    if (highlight) {
      OreHighlighter.render(matrices, camera, outlines);
    }

    original.call(outlines);

    if (highlight) {
      entityOutlinePostProcessor.render(tickCounter.getLastFrameDuration());
      client.getFramebuffer().beginWrite(false);
      gemstones$outlinesProcessed = true;
    }
  }

  // Glowing entities share the same pass, a second run would draw every outline twice
  @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(F)V"))
  private void gemstones$skipRepeatedOutlinePass(PostEffectProcessor processor, float tickDelta,
      Operation<Void> original) {
    if (gemstones$outlinesProcessed && processor == entityOutlinePostProcessor) {
      return;
    }

    original.call(processor, tickDelta);
  }
}
