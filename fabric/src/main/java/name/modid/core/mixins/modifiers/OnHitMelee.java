package name.modid.core.mixins.modifiers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
  @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
  private boolean applySlashModifiers(Entity target, DamageSource source, float damage) {
    PlayerEntity player = (PlayerEntity) (Object) this;

    if (!(player.getWorld() instanceof ServerWorld serverWorld)
        || !(player instanceof ServerPlayerEntity serverPlayer)
        || !(target instanceof LivingEntity livingTarget)) {
      return target.damage(source, damage);
    }

    float modifiedDamage = CustomEvents.ON_HIT_MELEE.invoker().onHitMelee(serverPlayer, livingTarget, damage);
    boolean damaged = livingTarget.damage(source, modifiedDamage);

    if (damaged) {
      EventMeleeEffect.apply(serverPlayer, serverWorld, livingTarget, modifiedDamage);
    }

    return damaged;
  }
}
