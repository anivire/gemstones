package name.modid.core.content.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class GuardianSmiteEffect extends StatusEffect {
  public GuardianSmiteEffect() {
    super(StatusEffectCategory.HARMFUL, 0x1c7cff);
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
