package name.modid.core.content.entities;

import org.joml.Vector3f;

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
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SparkProjectileEntity extends PersistentProjectileEntity {
  private static final int HOMING_DELAY = 10;
  private final float SPARK_DAMAGE = 3.0F;

  private final double maxSpeed;

  public SparkProjectileEntity(EntityType<? extends SparkProjectileEntity> type, World world) {
    super(type, world);
    this.setNoGravity(true);
    this.pickupType = PickupPermission.DISALLOWED;
    this.maxSpeed = 0.4 + world.random.nextDouble() * 0.2;
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
    super.tick();

    if (this.getWorld().isClient) {
      Vec3d vel = this.getVelocity();
      double px = this.getX() - vel.x * 1.2;
      double py = this.getY() - vel.y * 1.2;
      double pz = this.getZ() - vel.z * 1.2;

      double lifeFrac = Math.min(1.0, (double) this.age / 100.0);
      double spreadScale = 1.0 + 2.0 * lifeFrac;

      for (int i = 0; i < 3; i++) {
        double dx = (this.random.nextDouble() - 0.5) * 0.1 * spreadScale;
        double dy = (this.random.nextDouble() - 0.5) * 0.1 * spreadScale;
        double dz = (this.random.nextDouble() - 0.5) * 0.1 * spreadScale;

        this.getWorld().addParticle(
            ParticlesRegistry.SPARK_PARTICLE,
            px, py, pz,
            dx, dy, dz);
      }

      this.getWorld().addParticle(
          new DustParticleEffect(new Vector3f(1.0F, 1.0F, 0.0F), 1.0F),
          px, py, pz,
          0, 0, 0);
    }

    if (this.age >= HOMING_DELAY) {
      LivingEntity target = this.getWorld().getClosestEntity(
          LivingEntity.class,
          TargetPredicate.createAttackable().ignoreVisibility()
              .setPredicate(
                  (entity) -> entity != this.getOwner() && !(entity instanceof PlayerEntity)),
          null,
          this.getX(), this.getY(), this.getZ(),
          this.getBoundingBox().expand(10.0));

      if (target != null) {
        Vec3d toTarget = target.getEyePos().subtract(this.getPos()).normalize();
        Vec3d vel = this.getVelocity().add(toTarget.multiply(0.2));

        if (vel.lengthSquared() > maxSpeed * maxSpeed) {
          this.setVelocity(vel.normalize().multiply(maxSpeed));
        } else {
          this.setVelocity(vel);
        }
      }
    }

    if (this.age > 100) {
      this.discard();
    }
  }

  @Override
  protected void onEntityHit(EntityHitResult hit) {
    super.onEntityHit(hit);
    Entity entity = hit.getEntity();
    if (entity instanceof LivingEntity living
        && entity != this.getOwner()
        && this.getWorld() instanceof ServerWorld serverWorld) {
      serverWorld.spawnParticles(
          ParticleTypes.EXPLOSION,
          this.getX(), this.getY(), this.getZ(),
          1, 0, 0, 0, 0);
      living.timeUntilRegen = 0;
      living.damage(this.getDamageSources().magic(), SPARK_DAMAGE);
      this.discard();
    }
  }

  @Override
  protected void onBlockHit(BlockHitResult hit) {
    super.onBlockHit(hit);
    if (this.getWorld() instanceof ServerWorld serverWorld) {
      serverWorld.spawnParticles(
          ParticleTypes.EXPLOSION,
          this.getX(), this.getY(), this.getZ(),
          1, 0, 0, 0, 0);
      this.discard();
    }
  }

  @Override
  protected SoundEvent getHitSound() {
    return SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST;
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