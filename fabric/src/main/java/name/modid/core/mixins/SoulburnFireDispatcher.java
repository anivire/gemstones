package name.modid.core.mixins;

import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.features.SoulburnFireRenderer;
import name.modid.core.utils.accessors.SoulBurnEntityAccessor;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(EntityRenderDispatcher.class)
public abstract class SoulburnFireDispatcher {
  @Shadow
  private Quaternionf rotation;

  @Shadow
  public abstract <T extends Entity> EntityRenderer<? super T> getRenderer(T entity);

  // Drawn at the end of render with the same offset vanilla uses for fire, so no injection point
  // inside the method is needed
  @Inject(method = "render", at = @At("TAIL"))
  private <E extends Entity> void renderSoulburnFire(E entity,
      double x,
      double y,
      double z,
      float yaw,
      float tickDelta,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      CallbackInfo ci) {
    if (entity.doesRenderOnFire()
        || !(entity instanceof SoulBurnEntityAccessor accessor)
        || !accessor.hasSoulBurnEffect()) {
      return;
    }

    Vec3d offset = getRenderer(entity).getPositionOffset(entity, tickDelta);
    matrices.push();
    matrices.translate(x + offset.getX(), y + offset.getY(), z + offset.getZ());
    SoulburnFireRenderer.render(matrices,
        vertexConsumers,
        entity,
        MathHelper.rotateAround(MathHelper.Y_AXIS, rotation, new Quaternionf()));
    matrices.pop();
  }
}
