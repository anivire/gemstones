package name.modid.core.mixins.effects.witherGuard;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.utils.witherGuard.WitherSkullOrbitFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

@Mixin(WitherSkullEntity.class)
public abstract class PlayerWitherGuardEntity implements WitherSkullOrbitFlag {

  @Unique
  private boolean isOrbiting = false;

  @Override
  public void setOrbiting(boolean orbiting) {
    this.isOrbiting = orbiting;
  }

  @Override
  public boolean isOrbiting() {
    return this.isOrbiting;
  }

  @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
  private void cancelCollision(HitResult hitResult, CallbackInfo ci) {
    if (this.isOrbiting) {
      ci.cancel();
    }
  }

  public EntityDimensions getDimensions(EntityPose pose) {
    if (this.isOrbiting) {
      return EntityDimensions.fixed(0.5f, 0.5f);
    }
    return EntityDimensions.fixed(1.0f, 1.0f);
  }

  @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
  private void ignorePlayerHit(EntityHitResult hit, CallbackInfo ci) {
    Entity target = hit.getEntity();
    if (target instanceof PlayerEntity || this.isOrbiting) {
      ci.cancel();
    }
  }

}