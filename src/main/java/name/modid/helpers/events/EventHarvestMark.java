package name.modid.helpers.events;

import name.modid.effects.EffectRegistrationHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EventHarvestMark {
  public static final int MIN_ADDITIONAL_XP = 3;
  public static final int MAX_ADDITIONAL_XP = 5;

  public static void setupEvent(LivingEntity entity, DamageSource source) {
    if (entity.getWorld().isClient()) {
      return;
    }

    World world = entity.getWorld();
    StatusEffectInstance harvestMarkInstance =
        entity.getStatusEffect(EffectRegistrationHelper.HARVEST_MARK_EFFECT);

    if (harvestMarkInstance != null) {
      int stackCount = harvestMarkInstance.getAmplifier() + 1;
      int exp =
          (int) (Math.random() * (MAX_ADDITIONAL_XP - MIN_ADDITIONAL_XP + 1) + MIN_ADDITIONAL_XP);

      if (world instanceof ServerWorld serverWorld) {
        for (int i = 0; i < stackCount; i++) {
          serverWorld.spawnEntity(new ExperienceOrbEntity(serverWorld, entity.getX(), entity.getY(),
              entity.getZ(), exp));
        }
      }
    }
  }
}
