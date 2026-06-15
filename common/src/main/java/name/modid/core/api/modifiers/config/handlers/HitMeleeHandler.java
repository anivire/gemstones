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
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.core.utils.GetRandomBuff;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class HitMeleeHandler
    implements ModifierHandler<ModifierConfig.HitMeleeConfig> {
  private final Map<String, java.util.function.BiConsumer<List<GemstoneModifier>, ModifierContext>> handlers = Map.of(
      "ON_HIT_RANDOM_EFFECT", this::handleRandomEffect,
      "ON_HIT_LIFE_STEAL", this::handleLifesteal,
      "ON_HIT_MULTIPLY_DAMAGE_ARMORLESS", this::multiplyDamageArmorless,
      "ON_HIT_MAGIC_STRIKE", this::handleMagicStrike,
      "ON_HIT_EXP_ADDITIONAL_DAMAGE", this::handleAdditionalDamage,
      "ON_HIT_ENDER_JUDGEMENT", this::handleEnderJudgement);

  private static final List<String> ORDER = List.of(
      "ON_HIT_EXP_ADDITIONAL_DAMAGE",
      "ON_HIT_MULTIPLY_DAMAGE_ARMORLESS",
      "ON_HIT_MAGIC_STRIKE",
      "ON_HIT_LIFE_STEAL",
      "ON_HIT_RANDOM_EFFECT",
      "ON_HIT_ENDER_JUDGEMENT");

  @Override
  public boolean supports(GemstoneModifier modifier) {
    return modifier.getItemCategory() == ModifierItemCategory.MELEE
        || modifier.getItemCategory() == ModifierItemCategory.TOOLS;
  }

  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty()) {
      return;
    }

    // Default returned damage value for all further amage manipulations
    ctx.setDamageResult(ctx.getBaseDamageTaken());

    Map<String, List<GemstoneModifier>> grouped = modifiers.stream()
        .collect(Collectors.groupingBy(inst -> ((HitMeleeConfig) inst.getConfig()).eventType().getName()));

    for (String key : ORDER) {
      var group = grouped.get(key);
      if (group == null || group.isEmpty())
        continue;

      var fn = handlers.get(key);
      if (fn != null)
        fn.accept(group, ctx);
    }
  }

  private void handleEnderJudgement(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)) {
      return;
    }

    double healthPercent = target.getHealth() / target.getMaxHealth();
    double capDeathPercent = ModifierUtils.cappedProcChance(
        modifiers.stream()
            .map(m -> ((HitMeleeConfig) m.getConfig()).values().get(m.getRarityType()))
            .toList());

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
    }
  }

  private void handleMagicStrike(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)) {
      return;
    }

    double chance = ModifierUtils.cappedProcChance(
        modifiers.stream()
            .map(m -> ((HitMeleeConfig) m.getConfig()).values().get(m.getRarityType()))
            .toList());

    if (ModifierUtils.proc(ctx.getWorld(), chance)) {
      float bonusDamage = ctx.getDamageResult() * 0.3f;

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

    double chance = ModifierUtils.cappedProcChance(
        modifiers.stream()
            .map(m -> ((HitMeleeConfig) m.getConfig()).values().get(m.getRarityType()))
            .toList());

    if (ModifierUtils.proc(ctx.getWorld(), chance)) {
      float healAmount = (float) (ctx.getDamageResult() * 0.1 + 1.0);
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

  // NOTE: don't capped
  private void multiplyDamageArmorless(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner)
        || ModifierUtils.isArmorEquiped(owner)) {
      return;
    }

    float amplifierPercent = (float) modifiers.stream()
        .mapToDouble(m -> ((HitMeleeConfig) m.getConfig()).values().get(m.getRarityType()))
        .sum();

    ctx.setDamageResult(ctx.getDamageResult() * (1.0f + amplifierPercent));
  }

  private void handleRandomEffect(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)) {
      return;
    }

    double chance = ModifierUtils.cappedProcChance(
        modifiers.stream()
            .map(m -> ((HitMeleeConfig) m.getConfig()).values().get(m.getRarityType()))
            .toList());

    int duration = 15 * 20;
    int amplifier = ctx.getWorld().getRandom().nextInt(2);

    if (ModifierUtils.proc(ctx.getWorld(), chance)) {
      StatusEffectInstance buff = GetRandomBuff.negative(duration, amplifier);
      target.addStatusEffect(buff);
    }
  }

  private void handleAdditionalDamage(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof PlayerEntity player)
        || player.experienceLevel == 0) {
      return;
    }

    int levelOffset = 0;
    float bonusDamageAmplifierPercent = 0.0f;

    for (GemstoneModifier modifier : modifiers) {
      HitMeleeConfig config = (HitMeleeConfig) modifier.getConfig();
      levelOffset += config.additionalValues().get(modifier.getRarityType()).intValue();
      bonusDamageAmplifierPercent += config.values().get(modifier.getRarityType());
    }

    float extraDamageMultiplier = 1.0f + (player.experienceLevel / levelOffset) * bonusDamageAmplifierPercent;
    float newDamage = ctx.getDamageResult() * extraDamageMultiplier;

    ctx.setDamageResult(newDamage);
  }
}
