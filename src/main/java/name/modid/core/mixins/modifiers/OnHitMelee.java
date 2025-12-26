package name.modid.core.mixins.modifiers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.events.CustomEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(PlayerEntity.class)
public abstract class OnHitMelee {
  private Entity currentTarget;

  @Inject(method = "attack", at = @At("HEAD"))
  private void captureTarget(Entity target, CallbackInfo ci) {
    this.currentTarget = target;
  }

  @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
  private float applyDamageModifiers(float vanillaDamage) {
    PlayerEntity player = (PlayerEntity) (Object) this;

    if (!(player.getWorld() instanceof ServerWorld)
        || !(player instanceof ServerPlayerEntity serverPlayer)
        || !(currentTarget instanceof LivingEntity livingTarget)) {
      return vanillaDamage;
    }

    return CustomEvents.ON_HIT_MELEE.invoker().onHitMelee(serverPlayer, livingTarget, vanillaDamage);
  }

  @Inject(method = "attack", at = @At("TAIL"))
  private void clearTarget(Entity target, CallbackInfo ci) {
    this.currentTarget = null;
  }
}