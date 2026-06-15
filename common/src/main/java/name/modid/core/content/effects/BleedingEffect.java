package name.modid.core.content.effects;

import name.modid.core.content.registries.ParticlesRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class BleedingEffect extends StatusEffect {
  private static final int DAMAGE_INTERVAL_TICKS = 20;
  private static final int PARTICLE_INTERVAL_TICKS = 5;

  public BleedingEffect() {
    super(StatusEffectCategory.HARMFUL, 0xFF0000);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return true;
  }

  @Override
  public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
    if (!(entity.getWorld() instanceof ServerWorld)) {
      return true;
    }

    if (entity.age % DAMAGE_INTERVAL_TICKS == 0) {
      float damage = 1.0f + amplifier * 0.5f;
      entity.damage(entity.getDamageSources().generic(), damage);
    }

    if (entity.age % PARTICLE_INTERVAL_TICKS == 0) {
      spawnBleedParticles(entity, amplifier);
    }

    return true;
  }

  private void spawnBleedParticles(LivingEntity entity, int amplifier) {
    World world = entity.getWorld();

    int count = Math.min(10, 3 + amplifier * 2);
    double width = Math.max(0.25, entity.getWidth());
    double height = entity.getHeight();

    for (int i = 0; i < count; i++) {
      double angle = world.random.nextDouble() * Math.PI * 2.0;
      double radius = width * (0.2 + world.random.nextDouble() * 0.35);

      double x = entity.getX() + Math.cos(angle) * radius;
      double y = entity.getY() + height * (0.35 + world.random.nextDouble() * 0.45);
      double z = entity.getZ() + Math.sin(angle) * radius;

      double outwardSpeed = 0.025 + world.random.nextDouble() * 0.05;
      double velocityX = Math.cos(angle) * outwardSpeed + (world.random.nextDouble() - 0.5) * 0.025;
      double velocityY = -0.08 - world.random.nextDouble() * 0.08;
      double velocityZ = Math.sin(angle) * outwardSpeed + (world.random.nextDouble() - 0.5) * 0.025;

      spawnBleedParticle(world, x, y, z, velocityX, velocityY, velocityZ);
    }

    if (world.random.nextFloat() < 0.35F + amplifier * 0.08F) {
      double x = entity.getX() + (world.random.nextDouble() - 0.5) * width * 0.5;
      double y = entity.getY() + height * (0.15 + world.random.nextDouble() * 0.2);
      double z = entity.getZ() + (world.random.nextDouble() - 0.5) * width * 0.5;

      spawnBleedParticle(
          world,
          x,
          y,
          z,
          (world.random.nextDouble() - 0.5) * 0.02,
          -0.12 - world.random.nextDouble() * 0.08,
          (world.random.nextDouble() - 0.5) * 0.02);
    }
  }

  private void spawnBleedParticle(
      World world,
      double x,
      double y,
      double z,
      double velocityX,
      double velocityY,
      double velocityZ) {
    if (world instanceof ServerWorld serverWorld) {
      serverWorld.spawnParticles(
          ParticlesRegistry.BLEED_PARTICLE.get(),
          x,
          y,
          z,
          0,
          velocityX,
          velocityY,
          velocityZ,
          1.0);
    }
  }
}