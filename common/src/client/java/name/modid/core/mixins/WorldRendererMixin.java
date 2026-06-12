package name.modid.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import name.modid.core.api.OreHighlighter;
import net.minecraft.client.render.WorldRenderer;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
  // Rendering approach inspired by Leximon's Spelunker mod:
  // https://github.com/Leximon/Spelunker
  @ModifyVariable(
      method = "render",
      at = @At(value = "CONSTANT", args = "stringValue=blockentities", ordinal = 0),
      ordinal = 3)
  private boolean gemstones$renderOreVisionOutlinePostProcess(boolean value) {
    return value || OreHighlighter.hasActiveHighlights();
  }
}
