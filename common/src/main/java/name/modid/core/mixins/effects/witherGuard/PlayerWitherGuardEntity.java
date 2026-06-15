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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Mixin(WitherSkullEntity.class)
public abstract class PlayerWitherGuardEntity implements WitherSkullOrbitFlag {
  @Unique
  private static final double GUARD_SKULL_RADIUS = 2.0;

  @Unique
  private static final float GUARD_SKULL_DAMAGE = 8.0f;

  @Unique
  private static final int GUARD_SKULL_WITHER_TICKS = 5 * 20;

  @Unique
  private static final int GUARD_SKULL_WITHER_AMPLIFIER = 1;

  @Unique
  private boolean isOrbiting = false;

  @Unique
  private boolean isWitherGuardSkull = false;

  @Override
  public void setOrbiting(boolean orbiting) {
    this.isOrbiting = orbiting;
  }

  @Override
  public boolean isOrbiting() {
    return this.isOrbiting;
  }

  @Override
  public void setWitherGuardSkull(boolean witherGuardSkull) {
    this.isWitherGuardSkull = witherGuardSkull;
  }

  @Override
  public boolean isWitherGuardSkull() {
    return this.isWitherGuardSkull;
  }

  @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
  private void cancelCollision(HitResult hitResult, CallbackInfo ci) {
    if (this.isOrbiting) {
      ci.cancel();
      return;
    }

    if (this.isWitherGuardSkull) {
      handleWitherGuardImpact(hitResult);
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
    if (target instanceof PlayerEntity || this.isOrbiting || this.isWitherGuardSkull) {
      ci.cancel();
    }
  }

  @Unique
  private void handleWitherGuardImpact(HitResult hitResult) {
    WitherSkullEntity skull = (WitherSkullEntity) (Object) this;

    if (!(skull.getWorld() instanceof ServerWorld world)) {
      return;
    }

    Vec3d impactPos = hitResult.getType() == HitResult.Type.MISS ? skull.getPos() : hitResult.getPos();
    Entity owner = skull.getOwner();
    DamageSource damageSource = owner instanceof LivingEntity livingOwner
        ? skull.getDamageSources().witherSkull(skull, livingOwner)
        : skull.getDamageSources().magic();

    Box damageBox = new Box(impactPos, impactPos).expand(GUARD_SKULL_RADIUS);
    for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, damageBox, this::canDamageWitherGuardTarget)) {
      if (target == owner) {
        continue;
      }

      double distance = target.getBoundingBox().getCenter().distanceTo(impactPos);
      if (distance > GUARD_SKULL_RADIUS) {
        continue;
      }

      float damage = (float) (GUARD_SKULL_DAMAGE * (1.0 - distance / GUARD_SKULL_RADIUS));
      if (damage > 0.0f) {
        if (target.damage(damageSource, damage)) {
          target.addStatusEffect(new StatusEffectInstance(
              StatusEffects.WITHER,
              GUARD_SKULL_WITHER_TICKS,
              GUARD_SKULL_WITHER_AMPLIFIER));
        }
      }
    }

    world.spawnParticles(ParticleTypes.EXPLOSION, impactPos.x, impactPos.y, impactPos.z, 1, 0.0, 0.0, 0.0, 0.0);
    world.spawnParticles(ParticleTypes.SMOKE, impactPos.x, impactPos.y, impactPos.z, 12, 0.3, 0.3, 0.3, 0.02);
    world.playSound(null, impactPos.x, impactPos.y, impactPos.z,
        SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.6f, 1.3f);

    skull.discard();
  }

  @Unique
  private boolean canDamageWitherGuardTarget(LivingEntity target) {
    return target.isAlive()
        && !target.isSpectator()
        && target.canHit()
        && target instanceof Monster
        && !(target instanceof PlayerEntity);
  }
}
