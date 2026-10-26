package name.modid.core.mixins.modifiers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import name.modid.core.content.events.CustomEvents;
import name.modid.core.content.events.handlers.EventMeleeEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(PlayerEntity.class)
public abstract class OnHitMelee {
  @Unique
  private float gemstones$attackStrength = 1.0F;

  // The attack cooldown is reset inside attack() before the damage call, so it is
  // captured here
  @Inject(method = "attack", at = @At("HEAD"))
  private void captureAttackStrength(Entity target, CallbackInfo ci) {
    this.gemstones$attackStrength = ((PlayerEntity) (Object) this).getAttackCooldownProgress(0.5F);
  }

  // Other mods can wrap the same call and two redirects crash
  @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
  private boolean applySlashModifiers(Entity target, DamageSource source, float damage,
      Operation<Boolean> original) {
    PlayerEntity player = (PlayerEntity) (Object) this;

    if (!(player.getWorld() instanceof ServerWorld serverWorld)
        || !(player instanceof ServerPlayerEntity serverPlayer)
        || !(target instanceof LivingEntity livingTarget)) {
      return original.call(target, source, damage);
    }

    float attackStrength = this.gemstones$attackStrength;
    float modifiedDamage = CustomEvents.ON_HIT_MELEE.invoker().onHitMelee(serverPlayer, livingTarget, damage,
        attackStrength);
    boolean damaged = original.call(target, source, modifiedDamage);

    if (damaged) {
      EventMeleeEffect.apply(serverPlayer, serverWorld, livingTarget, modifiedDamage, attackStrength);
    }

    return damaged;
  }
}
