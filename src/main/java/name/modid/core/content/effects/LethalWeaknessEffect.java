package name.modid.core.content.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class LethalWeaknessEffect extends StatusEffect {
  public LethalWeaknessEffect() {
    super(StatusEffectCategory.HARMFUL, 0x7A2E8E);
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
