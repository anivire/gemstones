package name.modid.effects;

import name.modid.Gemstones;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

public class AmberBlessingEffect extends StatusEffect {
  public AmberBlessingEffect() {
    super(StatusEffectCategory.BENEFICIAL, 0xffb847);

    this.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
        Identifier.of(Gemstones.MOD_ID, "amber_blessing_movement"), 0.1,
        EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    this.addAttributeModifier(EntityAttributes.GENERIC_ARMOR,
        Identifier.of(Gemstones.MOD_ID, "amber_blessing_armor"), 4.0,
        EntityAttributeModifier.Operation.ADD_VALUE);
    this.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
        Identifier.of(Gemstones.MOD_ID, "amber_blessing_damage"), 0.75,
        EntityAttributeModifier.Operation.ADD_VALUE);
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
