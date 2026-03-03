package name.modid.core.utils;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;

import org.joml.Vector3f;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class HomingArrow {
  private static final WeakHashMap<PersistentProjectileEntity, LivingEntity> lockedTargets = new WeakHashMap<>();
  private static final Random random = new Random();

  private static final double MAX_TURN_DEGREES_PER_TICK = 10.0;
  private static final double INITIAL_SEARCH_RADIUS = 24.0;
  private static final double LOCK_BREAK_DISTANCE = 48.0;
  private static final int PREDICTION_TICKS = 4;
  private static final int HOMING_DELAY_TICKS = 4;

  private static final ParticleEffect HOMING_PARTICLE = ParticleTypes.DUST_PLUME;
  private static final double PARTICLE_OFFSET = 0.5;

  private static final Vector3f BASE_DUST_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);
  private static final float DUST_COLOR_VARIATION = 0.1f;
  private static final float DUST_PARTICLE_SCALE = 1.0f;
  private static final double DUST_PARTICLE_SPREAD = 0.1;

  public static void tickHomingArrow(PersistentProjectileEntity arrow) {
    if (!(arrow.getWorld() instanceof ServerWorld world)
        || arrow.isOnGround()
        || arrow.age < HOMING_DELAY_TICKS) {
      lockedTargets.remove(arrow);
      return;
    }

    LivingEntity currentTarget = lockedTargets.get(arrow);

    if (currentTarget != null) {
      boolean isTargetValid = currentTarget.isAlive()
          && !currentTarget.isSpectator()
          && arrow.squaredDistanceTo(currentTarget) < LOCK_BREAK_DISTANCE * LOCK_BREAK_DISTANCE;

      if (!isTargetValid) {
        currentTarget = null;
        lockedTargets.remove(arrow);
      }
    }

    if (currentTarget == null) {
      LivingEntity newTarget = findNearestTarget(world, arrow, INITIAL_SEARCH_RADIUS);
      if (newTarget != null) {
        currentTarget = newTarget;
        lockedTargets.put(arrow, newTarget);
      }
    }

    if (currentTarget == null) {
      return;
    }

    addHomingParticle(world, arrow);

    Vec3d currentVelocity = arrow.getVelocity();
    double currentSpeed = currentVelocity.length();
    if (currentSpeed < 0.01)
      return;

    Vec3d currentDirection = currentVelocity.normalize();
    Vec3d targetPos = currentTarget.getPos()
        .add(currentTarget.getVelocity().multiply(PREDICTION_TICKS))
        .add(0, currentTarget.getStandingEyeHeight() * 0.5, 0);
    Vec3d desiredDirection = targetPos.subtract(arrow.getPos()).normalize();

    double dotProduct = MathHelper.clamp(currentDirection.dotProduct(desiredDirection), -1.0, 1.0);
    double angleBetween = Math.toDegrees(Math.acos(dotProduct));

    Vec3d newDirection;
    if (angleBetween <= MAX_TURN_DEGREES_PER_TICK) {
      newDirection = desiredDirection;
    } else {
      double turnRatio = MAX_TURN_DEGREES_PER_TICK / angleBetween;
      newDirection = currentDirection.lerp(desiredDirection, turnRatio).normalize();
    }
    arrow.setVelocity(newDirection.multiply(currentSpeed));
  }

  private static void addHomingParticle(ServerWorld world, PersistentProjectileEntity arrow) {
    Vec3d velocity = arrow.getVelocity();
    if (velocity.lengthSquared() < 0.001) {
      return;
    }

    Vec3d direction = velocity.normalize();
    Vec3d spawnPos = arrow.getPos().subtract(direction.multiply(PARTICLE_OFFSET));

    world.spawnParticles(HOMING_PARTICLE, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 1, 0, 0, 0, 0);

    float r = BASE_DUST_COLOR.x() + (random.nextFloat() - 0.5f) * 2 * DUST_COLOR_VARIATION;
    float g = BASE_DUST_COLOR.y() + (random.nextFloat() - 0.5f) * 2 * DUST_COLOR_VARIATION;
    float b = BASE_DUST_COLOR.z() + (random.nextFloat() - 0.5f) * 2 * DUST_COLOR_VARIATION;

    r = MathHelper.clamp(r, 0.0f, 1.0f);
    g = MathHelper.clamp(g, 0.0f, 1.0f);
    b = MathHelper.clamp(b, 0.0f, 1.0f);

    DustParticleEffect dustEffect = new DustParticleEffect(new Vector3f(r, g, b), DUST_PARTICLE_SCALE);

    world.spawnParticles(dustEffect,
        spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(),
        3,
        DUST_PARTICLE_SPREAD,
        DUST_PARTICLE_SPREAD,
        DUST_PARTICLE_SPREAD,
        0.01);
  }

  private static LivingEntity findNearestTarget(ServerWorld world, PersistentProjectileEntity arrow, double radius) {
    Box searchBox = new Box(arrow.getBlockPos()).expand(radius);
    List<LivingEntity> potentialTargets = world.getEntitiesByClass(
        LivingEntity.class,
        searchBox,
        entity -> entity.isAlive() && !entity.isSpectator() && entity != arrow.getOwner());

    return potentialTargets.stream()
        .min(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(arrow)))
        .orElse(null);
  }
}