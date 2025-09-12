package name.modid.core.api.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class FreezingEffect extends StatusEffect {
  public FreezingEffect() {
    super(StatusEffectCategory.HARMFUL, 0x99d9ea);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return true;
  }

  @Override
  public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
    if (entity.hasStatusEffect(EffectsRegistry.FREEZING_EFFECT)
        && entity.getStatusEffect(EffectsRegistry.FREEZING_EFFECT).getDuration() <= 20) {
      entity.setFrozenTicks(0);
    } else {
      entity.setFrozenTicks(entity.getFrozenTicks() + 5);
    }
    return true;
  }

  @Override
  public void onApplied(LivingEntity entity, int amplifier) {
    super.onApplied(entity, amplifier);
  }

  @Override
  public void onEntityRemoval(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
    super.onEntityRemoval(entity, amplifier, reason);
    entity.setFrozenTicks(0);
  }
}