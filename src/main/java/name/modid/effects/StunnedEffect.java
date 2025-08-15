package name.modid.effects;

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
    return true;
  }
}
