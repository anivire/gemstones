package name.modid.core.utils.damage;

import name.modid.core.utils.accessors.DamageCooldownAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;

public final class DamageOverTimeHelper {
  private DamageOverTimeHelper() {
  }

  // Damage over time is credited to the player who applied it and does not consume
  // the target i-frames, so it does not weaken the next hit of the player
  public static void applyTick(LivingEntity entity, RegistryKey<DamageType> type, float amount) {
    if (entity.getWorld().isClient()) {
      return;
    }

    PlayerEntity attacker = DamageSourceHelper.getRecentPlayerAttacker(entity);
    int previousRegenTime = entity.timeUntilRegen;
    float previousLastDamage = entity instanceof DamageCooldownAccessor accessor
        ? accessor.gemstones$getLastDamageTaken()
        : 0.0F;

    entity.timeUntilRegen = 0;
    entity.damage(DamageSourceHelper.attributed(entity.getWorld(), type, attacker), amount);

    if (entity.isAlive()) {
      entity.timeUntilRegen = previousRegenTime;
      if (entity instanceof DamageCooldownAccessor accessor) {
        accessor.gemstones$setLastDamageTaken(previousLastDamage);
      }
    }
  }
}
