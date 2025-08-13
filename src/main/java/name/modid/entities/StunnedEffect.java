package name.modid.entities;

import name.modid.Gemstones;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

public class StunnedEffect extends StatusEffect {
  public StunnedEffect() {
    super(StatusEffectCategory.HARMFUL, 0xFFFF00);

    this.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
        Identifier.of(Gemstones.MOD_ID, "stunned_movement"), -1.0,
        EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    this.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED,
        Identifier.of(Gemstones.MOD_ID, "stunned_attack_speed"), -1.0,
        EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    this.addAttributeModifier(EntityAttributes.GENERIC_JUMP_STRENGTH,
        Identifier.of(Gemstones.MOD_ID, "stunned_jump"), -1.0,
        EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    this.addAttributeModifier(EntityAttributes.GENERIC_FOLLOW_RANGE,
        Identifier.of(Gemstones.MOD_ID, "stunned_follow_range"), -1.0,
        EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    this.addAttributeModifier(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE,
        Identifier.of(Gemstones.MOD_ID, "stunned_interaction"), -1.0,
        EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    this.addAttributeModifier(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE,
        Identifier.of(Gemstones.MOD_ID, "stunned_block_interaction"), -1.0,
        EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return true;
  }

  @Override
  public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
    // World world = entity.getWorld();
    // double headY = entity.getY() + entity.getHeight() + 0.4;
    // int particleCount = 4;
    // double radius = 0.5;
    // double speed = 0.2;

    // if (world.isClient) {
    // for (int i = 0; i < particleCount; i++) {
    // double angle = (entity.age * speed + (2 * Math.PI * i / particleCount)) % (2 * Math.PI);
    // double offsetX = radius * Math.cos(angle);
    // double offsetZ = radius * Math.sin(angle);

    // world.addParticle(new DustParticleEffect(new Vector3f(1f, 1f, 0f), 1f),
    // entity.getX() + offsetX, headY, entity.getZ() + offsetZ, 0, 0, 0);
    // }

    // } else if (world instanceof ServerWorld serverWorld) {
    // for (int i = 0; i < particleCount; i++) {
    // double angle = (entity.age * speed + (2 * Math.PI * i / particleCount)) % (2 * Math.PI);
    // double offsetX = radius * Math.cos(angle);
    // double offsetZ = radius * Math.sin(angle);

    // DustParticleEffect particle = new DustParticleEffect(new Vector3f(1f, 1f, 0f), 1f);
    // serverWorld.spawnParticles(particle, entity.getX() + offsetX, headY,
    // entity.getZ() + offsetZ, 1, 0, 0, 0, 0);
    // }
    // }

    return true;
  }
}
