package name.modid.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.WorldRenderer;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
  @Accessor("bufferBuilders")
  BufferBuilderStorage gemstones$getBufferBuilders();
}
