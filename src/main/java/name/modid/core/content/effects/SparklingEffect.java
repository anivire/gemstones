package name.modid.core.content.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SparklingEffect extends StatusEffect {
  public SparklingEffect() {
    super(StatusEffectCategory.BENEFICIAL, 0xFFD700);
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