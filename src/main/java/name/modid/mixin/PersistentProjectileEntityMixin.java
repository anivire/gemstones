package name.modid.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.helpers.GemstoneSocketingHelper;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.category.ModifierOnHitEffectProjectile;
import name.modid.helpers.modifiers.category.ModifierOnHitProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {
  @Inject(method = "onEntityHit", at = @At("HEAD"))
  protected void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
    handleHit(entityHitResult);
  }

  @Inject(method = "onBlockHit", at = @At("HEAD"))
  protected void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
    handleHit(blockHitResult);
  }

  private void handleHit(HitResult hitResult) {
    PersistentProjectileEntity entity = (PersistentProjectileEntity) (Object) this;
    ArrowEntity arrow = (ArrowEntity) entity;

    if (!(arrow.getOwner() instanceof PlayerEntity player)) {
      return;
    }

    ItemStack itemStack = GemstoneSocketingHelper.getWeaponStack(player);
    if (itemStack == null)
      return;

    if (arrow.getWorld() instanceof ServerWorld serverWorld) {
      Vec3d pos = hitResult.getPos();

      LivingEntity target = hitResult instanceof EntityHitResult entityHit
          && entityHit.getEntity() instanceof LivingEntity living ? living : null;

      // ON_HIT_EFFECT_PROJ
      ArrayList<ModifierOnHitEffectProjectile> onHitEffectProjectileModifiers = ModifierHelper
          .getOnHitEffectProjectileModifiers(itemStack);
      if (!onHitEffectProjectileModifiers.isEmpty() && target != null) {
        GemstoneSocketingHelper.applyOnHitEffectProjectileModifiers(
            onHitEffectProjectileModifiers,
            itemStack.getItem(),
            itemStack,
            target,
            serverWorld);
      }

      // ON_HIT
      ArrayList<ModifierOnHitProjectile> onHitModifiers = ModifierHelper.getOnHitProjectileModifiers(itemStack);
      if (!onHitModifiers.isEmpty()) {
        GemstoneSocketingHelper.applyOnHitProjectileModifiers(
            onHitModifiers,
            itemStack,
            serverWorld,
            pos,
            arrow,
            target);
      }
    }
  }
}
