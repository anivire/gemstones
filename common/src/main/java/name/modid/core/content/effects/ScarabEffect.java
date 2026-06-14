package name.modid.core.content.effects;

import name.modid.Gemstones;
import name.modid.core.content.particles.ScarabParticleInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ScarabEffect extends StatusEffect {
  public ScarabEffect() {
    super(StatusEffectCategory.HARMFUL, 0xec810f);

    this.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
        Identifier.of(Gemstones.MOD_ID, "scarab_movement"), -0.35,
        EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    this.addAttributeModifier(EntityAttributes.GENERIC_ARMOR,
        Identifier.of(Gemstones.MOD_ID, "scarab_armor"), -4.0,
        EntityAttributeModifier.Operation.ADD_VALUE);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return true;
  }

  @Override
  public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
    World world = entity.getWorld();

    if (world instanceof ServerWorld serverWorld && world.getTime() % 6 == 0) {
      ScarabParticleInstance particleEffect = new ScarabParticleInstance(entity.getId());

      serverWorld.spawnParticles(
          particleEffect,
          entity.getX(),
          entity.getY() + entity.getHeight() * 0.5,
          entity.getZ(),
          1,
          0.2, 0.2, 0.2,
          0.0);
    }

    return true;
  }
}