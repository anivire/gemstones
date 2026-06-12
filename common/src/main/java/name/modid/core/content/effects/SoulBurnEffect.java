package name.modid.core.content.effects;

import name.modid.Gemstones;
import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SoulBurnEffect extends StatusEffect {
  public SoulBurnEffect() {
    super(StatusEffectCategory.HARMFUL, 0x1E90FF, ParticleTypes.ASH);

    this.addAttributeModifier(EntityAttributes.GENERIC_ARMOR,
        Identifier.of(Gemstones.MOD_ID, "soul_burn_armor"), -2.5,
        EntityAttributeModifier.Operation.ADD_VALUE);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    int ticksBetweenDamage = 20;
    if (amplifier > 0) {
      ticksBetweenDamage = Math.max(1, 20 - (amplifier * 3));
    }
    return duration % ticksBetweenDamage == 0;
  }

  @Override
  public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
    World world = entity.getWorld();

    if (canApplyUpdateEffect(entity.getStatusEffect(EffectsRegistry.SOUL_BURN_EFFECT).getDuration(),
        amplifier)) {
      float damage = 1.0f + (amplifier * 0.5f);
      DamageSource damageSource = world.getDamageSources().onFire();
      entity.damage(damageSource, damage);
      entity.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F);
    }

    if (!world.isClient && world.getRandom().nextInt(2) == 0) {
      ServerWorld serverWorld = (ServerWorld) world;
      Random random = world.getRandom();

      int particleCount = 3 + amplifier * 2;
      for (int i = 0; i < particleCount; i++) {
        double offsetX = (random.nextDouble() - 0.5) * entity.getWidth() * 1.2;
        double offsetY = random.nextDouble() * entity.getHeight();
        double offsetZ = (random.nextDouble() - 0.5) * entity.getWidth() * 1.2;

        double velocityX = (random.nextDouble() - 0.5) * 0.3;
        double velocityY = random.nextDouble() * 0.2 + 0.05;
        double velocityZ = (random.nextDouble() - 0.5) * 0.3;
        serverWorld.spawnParticles(
            ParticleTypes.SOUL,
            entity.getX() + offsetX,
            entity.getY() + offsetY,
            entity.getZ() + offsetZ,
            1,
            velocityX, velocityY, velocityZ,
            0.08);
      }

      if (world.getTime() % 20 == 0) {
        for (int i = 0; i < 5 + amplifier; i++) {
          double angle = random.nextDouble() * Math.PI * 2;
          double force = 0.15 + random.nextDouble() * 0.25;

          double velocityX = Math.cos(angle) * force;
          double velocityY = random.nextDouble() * 0.3 + 0.1;
          double velocityZ = Math.sin(angle) * force;

          serverWorld.spawnParticles(
              ParticleTypes.SOUL,
              entity.getX(),
              entity.getY() + entity.getHeight() * 0.5,
              entity.getZ(),
              1,
              velocityX, velocityY, velocityZ,
              0.1);
        }

        world.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
            SoundEvents.PARTICLE_SOUL_ESCAPE,
            SoundCategory.HOSTILE, 1.0F, 1.5F);
      }
    }

    return true;
  }
}