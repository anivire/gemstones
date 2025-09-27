package name.modid.core.mixins.modifiers;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.HitProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

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

    if (!(projectile.getOwner() instanceof PlayerEntity player)) {
      return;
    }

    ItemStack itemStack = player.getMainHandStack();
    if (itemStack.isEmpty()) {
      return;
    }

    if (!(projectile.getWorld() instanceof ServerWorld serverWorld)) {
      return;
    }

    List<GemstoneModifier> modifiers = ModifierGatheringHelper.getModifiers(
        itemStack,
        HitProjectileConfig.class);

    if (modifiers.isEmpty()) {
      return;
    }

    ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(player)
        .withProjectile(projectile)
        .withBlockPos(BlockPos.ofFloored(hitResult.getPos()));

    if (hitResult instanceof EntityHitResult ehr
        && ehr.getEntity() instanceof LivingEntity living) {
      ctxBuilder.withTarget(living);
    }

    ModifierContext ctx = ctxBuilder.build();
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);
  }
}
