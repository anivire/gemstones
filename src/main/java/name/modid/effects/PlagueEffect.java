package name.modid.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class PlagueEffect extends StatusEffect {
  public PlagueEffect() {
    super(StatusEffectCategory.HARMFUL, 0x00a327);
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return true;
  }

  @Override
  public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
    if (!entity.getWorld().isClient) {
      if (entity.age % 60 == 0) {
        float damage = 1.5f + amplifier;

        entity.damage(
            ((ServerWorld) entity.getWorld()).getDamageSources().magic(),
            damage);
      }
    }
    return true;
  }
}
