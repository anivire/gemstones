package name.modid.core.utils.damage;

import org.jetbrains.annotations.Nullable;

import name.modid.core.content.entities.SparkProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

public final class DamageSourceHelper {
  private DamageSourceHelper() {
  }

  // Damage credited to the attacker without a source position, so knockback behaves like the unattributed source
  public static DamageSource attributed(World world, RegistryKey<DamageType> type, @Nullable Entity attacker) {
    RegistryEntry<DamageType> entry = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(type);
    return attacker == null ? new DamageSource(entry) : new DamageSource(entry, (Entity) null, attacker);
  }

  // Player who recently damaged the entity, used to credit damage over time and kills
  @Nullable
  public static PlayerEntity getRecentPlayerAttacker(LivingEntity victim) {
    LivingEntity adversary = victim.getPrimeAdversary();
    return adversary instanceof PlayerEntity player && player.isAlive() ? player : null;
  }

  @Nullable
  public static Entity getKiller(LivingEntity victim, DamageSource source) {
    Entity attacker = source.getAttacker();
    return attacker != null ? attacker : getRecentPlayerAttacker(victim);
  }

  // Direct player hits only: melee attacks and arrows, but not bonus damage, damage over time or sparks
  public static boolean isDirectPlayerHit(DamageSource source) {
    if (!(source.getAttacker() instanceof PlayerEntity)) {
      return false;
    }

    return source.isOf(DamageTypes.PLAYER_ATTACK)
        || (source.isOf(DamageTypes.ARROW) && !(source.getSource() instanceof SparkProjectileEntity));
  }
}
