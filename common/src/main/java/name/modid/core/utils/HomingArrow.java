package name.modid.core.utils;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;

import org.joml.Vector3f;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class HomingArrow {
  private static final WeakHashMap<PersistentProjectileEntity, LivingEntity> lockedTargets = new WeakHashMap<>();
  private static final WeakHashMap<PersistentProjectileEntity, LivingEntity> preferredTargets = new WeakHashMap<>();
  private static final Random random = new Random();

  private static final double MAX_TURN_DEGREES_PER_TICK = 10.0;
  private static final double INITIAL_SEARCH_RADIUS = 24.0;
  private static final double LOCK_BREAK_DISTANCE = 48.0;
  private static final double AIM_RAYCAST_DISTANCE = 64.0;
  private static final double AIM_CONE_DEGREES = 10.0;
  private static final int PREDICTION_TICKS = 4;
  private static final int HOMING_DELAY_TICKS = 4;

  private static final ParticleEffect HOMING_PARTICLE = ParticleTypes.DUST_PLUME;
  private static final double PARTICLE_OFFSET = 0.5;

  private static final Vector3f BASE_DUST_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);
  private static final float DUST_COLOR_VARIATION = 0.1f;
  private static final float DUST_PARTICLE_SCALE = 1.0f;
  private static final double DUST_PARTICLE_SPREAD = 0.1;

  public static LivingEntity findPreferredTarget(ServerWorld world, PlayerEntity shooter) {
    Vec3d start = shooter.getEyePos();
    Vec3d direction = shooter.getRotationVec(1.0F).normalize();
    return findBestTargetInCone(world, shooter, shooter, start, direction, AIM_RAYCAST_DISTANCE, AIM_CONE_DEGREES);
  }

  private static LivingEntity findBestTargetInCone(ServerWorld world,
      Entity owner,
      Entity raycastEntity,
      Vec3d start,
      Vec3d direction,
      double radius,
      double coneDegrees) {
    Vec3d normalizedDirection = direction.normalize();
    double coneCosine = Math.cos(Math.toRadians(coneDegrees));
    double maxConeRadius = Math.tan(Math.toRadians(coneDegrees)) * radius;
    Box searchBox = new Box(start, start.add(normalizedDirection.multiply(radius)))
        .expand(maxConeRadius + 1.0);

    List<LivingEntity> candidates = world.getEntitiesByClass(
        LivingEntity.class,
        searchBox,
        entity -> isTargetable(entity, owner));

    return candidates.stream()
        .filter(entity -> isInCone(start, normalizedDirection, entity, radius, coneCosine))
        .filter(entity -> hasLineOfSight(world, start, getAimPoint(start, entity), raycastEntity))
        .min(Comparator.comparingDouble(entity -> getConeScore(start, normalizedDirection, entity)))
        .orElse(null);
  }

  private static boolean isInCone(Vec3d start,
      Vec3d direction,
      LivingEntity entity,
      double radius,
      double coneCosine) {
    Vec3d toTarget = getAimPoint(start, entity).subtract(start);
    double distanceSquared = toTarget.lengthSquared();

    if (distanceSquared <= 1.0E-6 || distanceSquared > radius * radius) {
      return false;
    }

    return direction.dotProduct(toTarget.normalize()) >= coneCosine;
  }

  private static double getConeScore(Vec3d start, Vec3d direction, LivingEntity entity) {
    Vec3d toTarget = getAimPoint(start, entity).subtract(start);
    double distance = toTarget.length();
    double dot = MathHelper.clamp(direction.dotProduct(toTarget.normalize()), -1.0, 1.0);
    double angle = Math.toDegrees(Math.acos(dot));

    return angle * 100.0 + distance * 0.05;
  }

  private static boolean hasLineOfSight(ServerWorld world, Vec3d start, Vec3d end, Entity raycastEntity) {
    HitResult blockHit = world.raycast(new RaycastContext(
        start,
        end,
        RaycastContext.ShapeType.COLLIDER,
        RaycastContext.FluidHandling.NONE,
        raycastEntity));

    return blockHit.getType() == HitResult.Type.MISS
        || start.squaredDistanceTo(blockHit.getPos()) + 1.0 >= start.squaredDistanceTo(end);
  }

  public static void setPreferredTarget(PersistentProjectileEntity arrow, LivingEntity target) {
    if (target == null) {
      preferredTargets.remove(arrow);
      return;
    }

    preferredTargets.put(arrow, target);
  }

  public static void tickHomingArrow(PersistentProjectileEntity arrow) {
    if (!(arrow.getWorld() instanceof ServerWorld world)
        || arrow.isOnGround()) {
      lockedTargets.remove(arrow);
      preferredTargets.remove(arrow);
      return;
    }

    if (arrow.age < HOMING_DELAY_TICKS) {
      return;
    }

    LivingEntity currentTarget = lockedTargets.get(arrow);

    if (currentTarget != null) {
      if (!isTargetValid(arrow, currentTarget)) {
        currentTarget = null;
        lockedTargets.remove(arrow);
      }
    }

    if (currentTarget == null) {
      LivingEntity preferredTarget = preferredTargets.remove(arrow);
      if (isTargetValid(arrow, preferredTarget)) {
        currentTarget = preferredTarget;
        lockedTargets.put(arrow, preferredTarget);
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
    Vec3d targetPos = getAimPoint(arrow.getPos(), currentTarget)
        .add(currentTarget.getVelocity().multiply(PREDICTION_TICKS))
        .add(0, currentTarget.getHeight() * 0.1, 0);
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
    arrow.velocityDirty = true;
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
        entity -> isTargetable(entity, arrow.getOwner()));

    return potentialTargets.stream()
        .min(Comparator.comparingDouble(entity -> entity.squaredDistanceTo(arrow)))
        .orElse(null);
  }

  private static boolean isTargetValid(PersistentProjectileEntity arrow, LivingEntity target) {
    return target != null
        && isTargetable(target, arrow.getOwner())
        && arrow.squaredDistanceTo(target) < LOCK_BREAK_DISTANCE * LOCK_BREAK_DISTANCE
        && arrow.getWorld() instanceof ServerWorld world
        && hasLineOfSight(world, arrow.getPos(), getAimPoint(arrow.getPos(), target), arrow);
  }

  private static Vec3d getAimPoint(Vec3d from, LivingEntity entity) {
    if (entity instanceof EnderDragonEntity dragon) {
      EnderDragonPart closestPart = null;
      double closestDistance = Double.MAX_VALUE;

      for (EnderDragonPart part : dragon.getBodyParts()) {
        double distance = part.getBoundingBox().getCenter().squaredDistanceTo(from);

        if (distance < closestDistance) {
          closestDistance = distance;
          closestPart = part;
        }
      }

      if (closestPart != null) {
        return closestPart.getBoundingBox().getCenter();
      }
    }

    return entity.getPos().add(0.0, entity.getStandingEyeHeight() * 0.5, 0.0);
  }

  private static boolean isTargetable(LivingEntity entity, Entity owner) {
    return entity.isAlive()
        && !entity.isSpectator()
        && (entity.canHit() || entity instanceof EnderDragonEntity)
        && entity != owner
        && !(entity instanceof EndermanEntity);
  }
}
