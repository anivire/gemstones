package name.modid.core.utils;

import java.util.List;

import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;

public class GetRandomBuff {
  private static final Random RANDOM = Random.create();
  private static final List<RegistryEntry<StatusEffect>> NEGATIVE_EFFECTS = List.of(
      StatusEffects.POISON,
      StatusEffects.WEAKNESS,
      StatusEffects.SLOWNESS,
      StatusEffects.BLINDNESS,
      StatusEffects.MINING_FATIGUE,
      StatusEffects.HUNGER,
      StatusEffects.WITHER,
      StatusEffects.GLOWING,
      StatusEffects.DARKNESS,
      StatusEffects.INFESTED,
      EffectsRegistry.BLEEDING_EFFECT,
      EffectsRegistry.FREEZING_EFFECT,
      EffectsRegistry.GUARDIAN_SMITE_EFFECT,
      EffectsRegistry.PLAGUE_EFFECT,
      EffectsRegistry.SCARAB_EFFECT,
      EffectsRegistry.SOUL_BURN_EFFECT,
      EffectsRegistry.STUNNED_EFFECT,
      EffectsRegistry.HARVEST_MARK_EFFECT);
  private static final List<RegistryEntry<StatusEffect>> POSITIVE_EFFECTS = List.of(
      StatusEffects.SPEED,
      StatusEffects.HASTE,
      StatusEffects.REGENERATION,
      StatusEffects.STRENGTH,
      StatusEffects.RESISTANCE,
      StatusEffects.FIRE_RESISTANCE,
      StatusEffects.WATER_BREATHING,
      StatusEffects.CONDUIT_POWER,
      StatusEffects.HEALTH_BOOST,
      StatusEffects.ABSORPTION,
      StatusEffects.INVISIBILITY,
      StatusEffects.JUMP_BOOST,
      StatusEffects.LUCK,
      StatusEffects.SLOW_FALLING,
      StatusEffects.DOLPHINS_GRACE,
      StatusEffects.HERO_OF_THE_VILLAGE,
      EffectsRegistry.AMBER_BLESSING_EFFECT);

  private static StatusEffectInstance create(RegistryEntry<StatusEffect> effect, int buffDuration, int amplifier) {
    return new StatusEffectInstance(effect, buffDuration, amplifier, false, true, true);
  }

  public static StatusEffectInstance positive(int buffDuration, int amplifier) {
    RegistryEntry<StatusEffect> effect = POSITIVE_EFFECTS.get(RANDOM.nextInt(POSITIVE_EFFECTS.size()));
    return create(effect, buffDuration, amplifier);
  }

  public static StatusEffectInstance negative(int buffDuration, int amplifier) {
    RegistryEntry<StatusEffect> effect = NEGATIVE_EFFECTS.get(RANDOM.nextInt(NEGATIVE_EFFECTS.size()));
    return create(effect, buffDuration, amplifier);
  }
}