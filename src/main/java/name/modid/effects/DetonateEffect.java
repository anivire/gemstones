package name.modid.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class DetonateEffect extends StatusEffect {
  public DetonateEffect() {
    super(StatusEffectCategory.HARMFUL, 0xdb001f);
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
