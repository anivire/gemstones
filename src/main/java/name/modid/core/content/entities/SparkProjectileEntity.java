package name.modid.core.content.entities;

import name.modid.core.content.registries.EntitiesRegistry;
import name.modid.core.content.registries.ParticlesRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class SparkProjectileEntity extends PersistentProjectileEntity {
  private static final int HOMING_DELAY = 10;
  private static final int MAX_AGE = 20 * 10;
  private static final double HOMING_ACCELERATION = 0.08;
  private static final double AVOIDANCE_ACCELERATION = 0.12;
  private static final double WALL_CHECK_DISTANCE = 0.8;
  private final float SPARK_DAMAGE = 3.0F;

  private final double maxSpeed;

  public SparkProjectileEntity(EntityType<? extends SparkProjectileEntity> type, World world) {
    super(type, world);
    this.setNoGravity(true);
    this.setNoClip(true);
    this.pickupType = PickupPermission.DISALLOWED;
    this.maxSpeed = 0.22 + world.random.nextDouble() * 0.10;
  }

  public SparkProjectileEntity(World world, double x, double y, double z) {
    this(EntitiesRegistry.SPARK_ENTITY, world);
    this.setPosition(x, y, z);
  }

  public SparkProjectileEntity(World world, LivingEntity owner) {
    this(EntitiesRegistry.SPARK_ENTITY, world);
    this.setOwner(owner);
    this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
  }

  @Override
  public ItemStack getDefaultItemStack() {
    return new ItemStack(Items.FIRE_CHARGE);
  }

  @Override
  public void tick() {
    this.inGround = false;
    this.inGroundTime = 0;
    this.setNoClip(true);

    LivingEntity target = this.age >= HOMING_DELAY ? getHomingTarget() : null;
    Vec3d velocity = this.getVelocity();

    if (target != null) {
      velocity = velocity.add(target.getEyePos().subtract(this.getPos()).normalize().multiply(HOMING_ACCELERATION));
    }

    velocity = slideAlongWall(velocity, target);

    if (target != null && isPathBlocked(velocity)) {
      velocity = velocity.add(getAvoidanceVector(target).multiply(AVOIDANCE_ACCELERATION));
    }

    this.setVelocity(clampVelocity(velocity));
    super.tick();
    this.inGround = false;
    this.inGroundTime = 0;
    this.setNoClip(true);

    if (this.getWorld().isClient) {
      Vec3d vel = this.getVelocity();
      double px = this.getX() - vel.x * 1.2;
      double py = this.getY() - vel.y * 1.2;
      double pz = this.getZ() - vel.z * 1.2;

      double lifeFrac = Math.min(1.0, (double) this.age / 100.0);
      double spreadScale = 1.0 + 20.0 * lifeFrac;

      for (int i = 0; i < 3; i++) {
        double dx = (this.random.nextDouble() - 0.5) * 0.1 * spreadScale;
        double dy = (this.random.nextDouble() - 0.5) * 0.1 * spreadScale;
        double dz = (this.random.nextDouble() - 0.5) * 0.1 * spreadScale;

        this.getWorld().addParticle(
            ParticlesRegistry.SPARK_PARTICLE,
            px, py, pz,
            dx, dy, dz);
      }
    }

    if (this.age > MAX_AGE) {
      this.discard();
    }
  }

  private LivingEntity getHomingTarget() {
    return this.getWorld().getClosestEntity(
        LivingEntity.class,
        TargetPredicate.createAttackable().ignoreVisibility()
            .setPredicate(
                (entity) -> entity != this.getOwner() && !(entity instanceof PlayerEntity)),
        null,
        this.getX(), this.getY(), this.getZ(),
        this.getBoundingBox().expand(10.0));
  }

  private boolean isPathBlocked(Vec3d velocity) {
    if (velocity.lengthSquared() < 1.0E-4) {
      return false;
    }

    Vec3d nextPos = this.getPos().add(velocity.normalize().multiply(0.75));
    BlockPos blockPos = BlockPos.ofFloored(nextPos);
    return !this.getWorld().getBlockState(blockPos)
        .getCollisionShape(this.getWorld(), blockPos)
        .isEmpty();
  }

  private Vec3d slideAlongWall(Vec3d velocity, LivingEntity target) {
    if (velocity.lengthSquared() < 1.0E-4) {
      return velocity;
    }

    Vec3d start = this.getPos();
    Vec3d end = start.add(velocity.normalize().multiply(Math.max(WALL_CHECK_DISTANCE, velocity.length() + 0.2)));
    HitResult hit = this.getWorld().raycast(new RaycastContext(
        start,
        end,
        RaycastContext.ShapeType.COLLIDER,
        RaycastContext.FluidHandling.NONE,
        this));

    if (!(hit instanceof BlockHitResult blockHit)
        || blockHit.getType() == HitResult.Type.MISS) {
      return velocity;
    }

    Direction side = blockHit.getSide();
    Vec3d normal = Vec3d.of(side.getVector());
    Vec3d slide = velocity.subtract(normal.multiply(velocity.dotProduct(normal)));

    if (slide.lengthSquared() < 1.0E-4 && target != null) {
      Vec3d desired = target.getEyePos().subtract(this.getPos()).normalize();
      slide = desired.subtract(normal.multiply(desired.dotProduct(normal))).multiply(velocity.length());
    }

    if (slide.lengthSquared() < 1.0E-4) {
      slide = getFallbackSlideVector(side).multiply(velocity.length());
    }

    Vec3d nudgedPos = blockHit.getPos().add(normal.multiply(0.08));
    this.setPosition(nudgedPos.x, nudgedPos.y, nudgedPos.z);
    return slide.normalize().multiply(Math.max(0.08, velocity.length() * 0.9));
  }

  private Vec3d getFallbackSlideVector(Direction side) {
    return switch (side.getAxis()) {
      case X -> new Vec3d(0.0, 0.35, 1.0).normalize();
      case Y -> new Vec3d(1.0, 0.0, 0.0);
      case Z -> new Vec3d(1.0, 0.35, 0.0).normalize();
    };
  }

  private Vec3d getAvoidanceVector(LivingEntity target) {
    Vec3d toTarget = target.getEyePos().subtract(this.getPos()).normalize();
    Vec3d sideways = new Vec3d(-toTarget.z, 0.0, toTarget.x).normalize();

    if ((this.age / 20) % 2 == 0) {
      sideways = sideways.multiply(-1.0);
    }

    return new Vec3d(sideways.x, 0.85, sideways.z).normalize();
  }

  private Vec3d clampVelocity(Vec3d velocity) {
    if (velocity.lengthSquared() > maxSpeed * maxSpeed) {
      return velocity.normalize().multiply(maxSpeed);
    }

    return velocity;
  }

  @Override
  protected void onEntityHit(EntityHitResult hit) {
    super.onEntityHit(hit);
    Entity entity = hit.getEntity();
    if (entity instanceof LivingEntity living
        && entity != this.getOwner()
        && this.getWorld() instanceof ServerWorld serverWorld) {
      serverWorld.spawnParticles(
          ParticleTypes.FLAME,
          this.getX(), this.getY(), this.getZ(),
          10,
          0.3, 0.3, 0.3,
          0.05);
      living.timeUntilRegen = 0;
      living.damage(this.getDamageSources().magic(), SPARK_DAMAGE);
      this.discard();
    }
  }

  @Override
  protected void onBlockHit(BlockHitResult hit) {
    this.inGround = false;
    this.inGroundTime = 0;
    this.setNoClip(true);
  }

  @Override
  protected SoundEvent getHitSound() {
    return SoundEvents.ENTITY_BLAZE_SHOOT;
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
  }
}
