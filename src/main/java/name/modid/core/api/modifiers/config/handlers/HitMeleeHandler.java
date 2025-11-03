package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitMeleeConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.utils.GetRandomBuff;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;

public class HitMeleeHandler
    implements ModifierHandler<ModifierConfig.HitMeleeConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    EventType type = ((HitMeleeConfig) modifiers.get(0).getConfig()).eventType();

    switch (type) {
      case ON_HIT_LIFE_STEAL -> handleLifesteal(modifiers, ctx);
      case ON_HIT_MULTIPLY_DAMAGE_ARMORLESS -> multiplyDamageArmorless(modifiers, ctx);
      case ON_HIT_RANDOM_EFFECT -> handleRandomEffect(modifiers, ctx);
      default -> {
      }
    }
  }

  private void handleLifesteal(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity attacker)
        || ctx.getTarget() == null) {
      return;
    }

    double combinedChance = 0.0;
    for (GemstoneModifier modifier : modifiers) {
      HitMeleeConfig config = (HitMeleeConfig) modifier.getConfig();
      combinedChance += config.chance().get(modifier.getRarityType());
    }

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      float healAmount = (float) (ctx.getBaseDamageTaken() * 0.1 + 1.0);
      attacker.heal(healAmount);

      ctx.getWorld().playSound(null, attacker.getBlockPos(),
          SoundEvents.ENTITY_PHANTOM_BITE,
          SoundCategory.PLAYERS,
          0.5f, 0.8f);

      ctx.getWorld().spawnParticles(ParticleTypes.HEART,
          attacker.getX(),
          attacker.getBodyY(0.5),
          attacker.getZ(),
          6, 0.6, 0.6, 0.6, 0.4);
    }

    ctx.setDamageResult(ctx.getBaseDamageTaken());
  }

  private void multiplyDamageArmorless(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (ctx.getOwner() == null) {
      return;
    }

    float additionalDamagePercent = 0.0F;
    for (GemstoneModifier modifier : modifiers) {
      HitMeleeConfig config = (HitMeleeConfig) modifier.getConfig();
      additionalDamagePercent += config.chance().get(modifier.getRarityType());
    }

    if (ctx.getOwner() instanceof LivingEntity livingEntity
        && !ModifierUtils.isArmorEquiped(livingEntity)) {
      ctx.setDamageResult(ctx.getBaseDamageTaken() + ctx.getBaseDamageTaken() * additionalDamagePercent);
    } else {
      ctx.setDamageResult(ctx.getBaseDamageTaken());
    }
  }

  private void handleRandomEffect(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (ctx.getTarget() == null) {
      return;
    }

    double combinedChance = 0.0;
    for (GemstoneModifier modifier : modifiers) {
      HitMeleeConfig config = (HitMeleeConfig) modifier.getConfig();
      combinedChance += config.chance().get(modifier.getRarityType());
    }

    Random random = ctx.getWorld().getRandom();
    int duration = 15 * 20;
    int amplifier = random.nextInt(2);

    if (ctx.getTarget() instanceof LivingEntity target
        && ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      StatusEffectInstance buff = GetRandomBuff.negative(duration, amplifier);
      target.addStatusEffect(buff);
    }

    ctx.setDamageResult(ctx.getBaseDamageTaken());
  }
}