package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;

public class HitMeleeHandler
    implements ModifierHandler<ModifierConfig.HitMeleeConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    Map<EventType, List<GemstoneModifier>> types = modifiers.stream()
        .collect(Collectors.groupingBy(
            m -> ((HitMeleeConfig) m.getConfig()).eventType(),
            () -> new java.util.EnumMap<>(EventType.class),
            Collectors.toList()));

    types.forEach((type, group) -> {
      switch (type) {
        case ON_HIT_LIFE_STEAL -> handleLifesteal(group, ctx);
        case ON_HIT_MULTIPLY_DAMAGE_ARMORLESS -> multiplyDamageArmorless(group, ctx);
        case ON_HIT_RANDOM_EFFECT -> handleRandomEffect(group, ctx);
        case ON_HIT_MAGIC_STRIKE -> handleMagicStrike(group, ctx);
        case ON_HIT_EXP_ADDITIONAL_DAMAGE -> handleAdditionalDamage(group, ctx);
        case ON_HIT_ENDER_JUDGEMENT -> handleEnderJudgement(group, ctx);
        default -> {
        }
      }
    });
  }

  private void handleEnderJudgement(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)) {
      return;
    }

    List<Double> capPercents = new ArrayList<>();

    for (GemstoneModifier modifier : modifiers) {
      HitMeleeConfig config = (HitMeleeConfig) modifier.getConfig();
      capPercents.add(config.chance().get(modifier.getRarityType()));
    }

    double capDeathPercent = ModifierUtils.combinedProcChance(capPercents);
    double healthPercent = target.getHealth() / target.getMaxHealth();

    if (healthPercent < capDeathPercent) {

      if (ctx.getWorld() instanceof ServerWorld serverWorld) {
        serverWorld.spawnParticles(
            ParticleTypes.REVERSE_PORTAL,
            target.getX(),
            target.getBodyY(0.5),
            target.getZ(),
            40,
            0.6, 1.0, 0.6, 0.05);
      }

      ctx.setDamageResult(Float.MAX_VALUE);
    } else {
      ctx.setDamageResult(ctx.getDamageResult() > 0.000 ? ctx.getDamageResult() : ctx.getBaseDamageTaken());
    }
  }

  private void handleMagicStrike(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)) {
      return;
    }

    List<Double> chances = new ArrayList<>();

    for (GemstoneModifier modifier : modifiers) {
      HitMeleeConfig config = (HitMeleeConfig) modifier.getConfig();
      chances.add(config.chance().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      float bonusDamage = ctx.getBaseDamageTaken() * 0.3f;

      target.getWorld().getServer().execute(() -> {
        if (target.isAlive()) {
          try {
            target.hurtTime = 0;
            target.timeUntilRegen = 0;
            target.damage(target.getDamageSources().magic(), bonusDamage);
          } finally {
          }
        }
      });
    }
  }

  private void handleLifesteal(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity attacker)
        || ctx.getTarget() == null) {
      return;
    }

    List<Double> chances = new ArrayList<>();

    for (GemstoneModifier modifier : modifiers) {
      HitMeleeConfig config = (HitMeleeConfig) modifier.getConfig();
      chances.add(config.chance().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

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
  }

  private void multiplyDamageArmorless(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (ctx.getOwner() == null) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      HitMeleeConfig config = (HitMeleeConfig) modifier.getConfig();
      chances.add(config.chance().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ctx.getOwner() instanceof LivingEntity livingEntity
        && !ModifierUtils.isArmorEquiped(livingEntity)) {
      ctx.setDamageResult(ctx.getBaseDamageTaken() + ctx.getBaseDamageTaken() * (float) combinedChance);
    } else {
      ctx.setDamageResult(ctx.getBaseDamageTaken());
    }
  }

  private void handleRandomEffect(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (ctx.getTarget() == null) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      HitMeleeConfig config = (HitMeleeConfig) modifier.getConfig();
      chances.add(config.chance().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    Random random = ctx.getWorld().getRandom();
    int duration = 15 * 20;
    int amplifier = random.nextInt(2);

    if (ctx.getTarget() instanceof LivingEntity target
        && ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      StatusEffectInstance buff = GetRandomBuff.negative(duration, amplifier);
      target.addStatusEffect(buff);
    }
  }

  private void handleAdditionalDamage(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity attacker)) {
      return;
    }

    int experienceLevel = attacker instanceof PlayerEntity player
        ? player.experienceLevel
        : 0;

    if (experienceLevel == 0) {
      return;
    }

    int levelOffset = 0;
    float bonusDamagePercent = 0.0f;
    for (GemstoneModifier modifier : modifiers) {
      HitMeleeConfig config = (HitMeleeConfig) modifier.getConfig();
      bonusDamagePercent += config.chance().get(modifier.getRarityType());
      levelOffset += config.additionValues().get(modifier.getRarityType()).intValue();
    }

    int bonusDamageSteps = experienceLevel / levelOffset;
    float extraDamageMultiplier = 1.0f + bonusDamageSteps * bonusDamagePercent;

    float baseDamage = ctx.getDamageResult() > 0
        ? ctx.getDamageResult()
        : ctx.getBaseDamageTaken();
    float newDamage = baseDamage * extraDamageMultiplier;

    ctx.setDamageResult(newDamage);
  }
}