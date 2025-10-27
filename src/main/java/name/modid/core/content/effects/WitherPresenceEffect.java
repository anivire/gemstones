package name.modid.core.content.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class WitherPresenceEffect extends StatusEffect {
  public WitherPresenceEffect() {
    super(StatusEffectCategory.BENEFICIAL, 0x4B0082);
  }

  @Override
  public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
    return true;
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return false;
  }
}