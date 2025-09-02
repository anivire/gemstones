package name.modid.helpers.events;

import name.modid.effects.registration.EffectRegistrationHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;

public class EventDetonate {
  private static final float EXPLOSIOM_POWER = 0.5F;

  public static void setupEvent(LivingEntity entity, DamageSource source) {
    if (entity.hasStatusEffect(EffectRegistrationHelper.DETONATE_EFFECT)) {
      entity.getWorld().createExplosion(
          entity,
          null,
          null,
          entity.getX(),
          entity.getY(),
          entity.getZ(),
          EXPLOSIOM_POWER + entity.getStatusEffect(EffectRegistrationHelper.DETONATE_EFFECT).getAmplifier(),
          false,
          World.ExplosionSourceType.MOB);
    }
  }
}
