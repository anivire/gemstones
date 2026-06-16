package name.modid.core.mixins;

import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.utils.accessors.SoulBurnEntityAccessor;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Mixin(EntityRenderDispatcher.class)
public abstract class SoulburnFireDispatcher {
  @Unique
  private static final SpriteIdentifier SOUL_FIRE_0 = new SpriteIdentifier(
      SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
      Identifier.of("minecraft", "block/soul_fire_0"));

  @Unique
  private static final SpriteIdentifier SOUL_FIRE_1 = new SpriteIdentifier(
      SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
      Identifier.of("minecraft", "block/soul_fire_1"));

  @Unique
  private Entity gemstones$currentFireEntity;

  @Shadow
  private Quaternionf rotation;

  @Shadow
  private void renderFire(MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      Entity entity,
      Quaternionf rotation) {
  }

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
      renderFire(matrices,
          vertexConsumers,
          entity,
          MathHelper.rotateAround(MathHelper.Y_AXIS, rotation, new Quaternionf()));
    }
  }

  @Inject(method = "renderFire", at = @At("HEAD"))
  private void captureSoulburnFireEntity(MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      Entity entity,
      Quaternionf rotation,
      CallbackInfo ci) {
    gemstones$currentFireEntity = entity;
  }

  @Inject(method = "renderFire", at = @At("RETURN"))
  private void clearSoulburnFireEntity(MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      Entity entity,
      Quaternionf rotation,
      CallbackInfo ci) {
    gemstones$currentFireEntity = null;
  }

  @Redirect(method = "renderFire", at = @At(value = "FIELD",
      target = "Lnet/minecraft/client/render/model/ModelLoader;FIRE_0:Lnet/minecraft/client/util/SpriteIdentifier;"))
  private SpriteIdentifier useSoulburnFire0() {
    return hasSoulburnFireEntity() ? SOUL_FIRE_0 : ModelLoader.FIRE_0;
  }

  @Redirect(method = "renderFire", at = @At(value = "FIELD",
      target = "Lnet/minecraft/client/render/model/ModelLoader;FIRE_1:Lnet/minecraft/client/util/SpriteIdentifier;"))
  private SpriteIdentifier useSoulburnFire1() {
    return hasSoulburnFireEntity() ? SOUL_FIRE_1 : ModelLoader.FIRE_1;
  }

  @Unique
  private boolean hasSoulburnFireEntity() {
    return gemstones$currentFireEntity instanceof SoulBurnEntityAccessor accessor
        && accessor.hasSoulBurnEffect();
  }
}
