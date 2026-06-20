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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Mixin(EntityRenderDispatcher.class)
public abstract class SoulburnFireDispatcher {
  @Shadow
  private Quaternionf rotation;

  @Inject(method = "render", at = @At(value = "INVOKE",
      target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
      shift = At.Shift.AFTER))
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
    if (!entity.doesRenderOnFire()
        && entity instanceof SoulBurnEntityAccessor accessor
        && accessor.hasSoulBurnEffect()) {
      SoulburnFireRenderer.render(matrices,
          vertexConsumers,
          entity,
          MathHelper.rotateAround(MathHelper.Y_AXIS, rotation, new Quaternionf()));
    }
  }
}
