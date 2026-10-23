package name.modid.core.utils.guardianSmite;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import name.modid.core.content.registries.EffectsRegistry;
import name.modid.core.utils.damage.DamageSourceHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;

// Guardian Smite: the next direct hits of a player get bonus damage inside the hit itself
public final class GuardianSmiteHelper {
  private static final int MAX_CHARGES = 2;
  private static final float BONUS_DAMAGE_PER_LEVEL = 3.0F;

  private static final Map<LivingEntity, Integer> USED_CHARGES = new WeakHashMap<>();
  private static final Set<LivingEntity> BOOSTED_HITS = Collections.newSetFromMap(new WeakHashMap<>());

  private GuardianSmiteHelper() {
  }

  public static void resetCharges(LivingEntity target) {
    USED_CHARGES.remove(target);
  }

  public static float applyBonus(LivingEntity target, DamageSource source, float amount) {
    if (target.getWorld().isClient() || !DamageSourceHelper.isDirectPlayerHit(source)) {
      return amount;
    }

    StatusEffectInstance effect = target.getStatusEffect(EffectsRegistry.guardianSmiteEntry());
    if (effect == null) {
      return amount;
    }

    BOOSTED_HITS.add(target);
    return amount + BONUS_DAMAGE_PER_LEVEL * (effect.getAmplifier() + 1);
  }

  // A charge is spent only when the boosted hit actually dealt damage
  public static void finishHit(LivingEntity target, boolean damaged) {
    if (!BOOSTED_HITS.remove(target) || !damaged) {
      return;
    }

    int used = USED_CHARGES.getOrDefault(target, 0) + 1;
    if (used >= MAX_CHARGES) {
      USED_CHARGES.remove(target);
      target.removeStatusEffect(EffectsRegistry.guardianSmiteEntry());
    } else {
      USED_CHARGES.put(target, used);
    }
  }
}
