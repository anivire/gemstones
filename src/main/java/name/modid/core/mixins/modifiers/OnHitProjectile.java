package name.modid.core.mixins.modifiers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.events.CustomEvents;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

@Mixin(PersistentProjectileEntity.class)
public class OnHitProjectile {
  @Inject(method = "onEntityHit", at = @At("HEAD"))
  protected void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
    handleHit(entityHitResult);
  }

  @Inject(method = "onBlockHit", at = @At("HEAD"))
  protected void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
    handleHit(blockHitResult);
  }

  private void handleHit(HitResult hitResult) {
    PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;

    if (!(projectile.getOwner() instanceof ServerPlayerEntity player)) {
      return;
    }

    CustomEvents.ON_HIT_PROJECTILE.invoker().onHitProjectile(projectile, player, hitResult);
  }
}