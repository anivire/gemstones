package name.modid.core.api.events;

import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;

public class EventDetonate {
  private static final float EXPLOSIOM_POWER = 0.5F;

  public static void setupEvent(LivingEntity entity, DamageSource source) {
    if (entity.hasStatusEffect(EffectsRegistry.DETONATE_EFFECT)) {
      entity.getWorld().createExplosion(
          entity,
          null,
          null,
          entity.getX(),
          entity.getY(),
          entity.getZ(),
          EXPLOSIOM_POWER + entity.getStatusEffect(EffectsRegistry.DETONATE_EFFECT).getAmplifier(),
          false,
          World.ExplosionSourceType.MOB);
    }
  }
}
