package name.modid.core.api.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class ExperienceThirstEffect extends StatusEffect {
  public ExperienceThirstEffect() {
    super(StatusEffectCategory.BENEFICIAL, 0xe9b8b3);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return true;
  }

  @Override
  public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
    if (entity instanceof PlayerEntity) {
      ((PlayerEntity) entity).addExperience(1 << amplifier);
    }

    return true;
  }
}
