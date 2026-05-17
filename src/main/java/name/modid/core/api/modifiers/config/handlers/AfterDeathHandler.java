package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.AfterDeathConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class AfterDeathHandler implements ModifierHandler<ModifierConfig.AfterDeathConfig> {
  private final Map<String, java.util.function.BiConsumer<List<GemstoneModifier>, ModifierContext>> handlers = Map.of(
      "AFTER_DEATH_DETONATE", this::handleDetonate,
      "AFTER_DEATH_HARVEST_MARK", this::handleHarvestMark,
      "AFTER_DEATH_ADDITIONAL_EXP_GAIN", this::handleBonusExpGain);

  private static final List<String> ORDER = List.of(
      "AFTER_DEATH_DETONATE",
      "AFTER_DEATH_HARVEST_MARK",
      "AFTER_DEATH_ADDITIONAL_EXP_GAIN");

  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty()) {
      return;
    }

    Map<String, List<GemstoneModifier>> grouped = modifiers.stream()
        .collect(Collectors.groupingBy(inst -> ((AfterDeathConfig) inst.getConfig()).eventType().getName()));

    for (String key : ORDER) {
      var group = grouped.get(key);
      if (group == null || group.isEmpty())
        continue;

      var fn = handlers.get(key);
      if (fn != null)
        fn.accept(group, ctx);
    }
  }

  private void handleDetonate(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)
        || !target.hasStatusEffect(EffectsRegistry.DETONATE_EFFECT)) {
      return;
    }

    target.getWorld().createExplosion(
        target,
        null,
        null,
        target.getX(),
        target.getY(),
        target.getZ(),
        0.5F + target.getStatusEffect(EffectsRegistry.DETONATE_EFFECT).getAmplifier(), false,
        World.ExplosionSourceType.MOB);
  }

  // NOTE: don't capped
  private void handleHarvestMark(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)
        || !target.hasStatusEffect(EffectsRegistry.HARVEST_MARK_EFFECT)) {
      return;
    }

    final int MIN_ADDITIONAL_XP = 3;
    final int MAX_ADDITIONAL_XP = 5;

    int stackCount = target.getStatusEffect(EffectsRegistry.HARVEST_MARK_EFFECT).getAmplifier() + 1;
    int exp = (int) (Math.random() * (MAX_ADDITIONAL_XP - MIN_ADDITIONAL_XP + 1) + MIN_ADDITIONAL_XP);

    for (int i = 0; i < stackCount; i++) {
      ctx.getWorld().spawnEntity(new ExperienceOrbEntity(
          ctx.getWorld(),
          target.getX(),
          target.getY(),
          target.getZ(),
          exp));
    }

  }

  private void handleBonusExpGain(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner)
        || !(ctx.getTarget() instanceof LivingEntity target)) {
      return;
    }

    float bonusPercent = (float) modifiers.stream()
        .mapToDouble(m -> ((AfterDeathConfig) m.getConfig()).values().get(m.getRarityType())).sum();

    if (bonusPercent <= 0.0f) {
      return;
    }

    int vanillaXp = target.getXpToDrop(ctx.getWorld(), owner);

    if (vanillaXp <= 0) {
      return;
    }

    float result = vanillaXp * bonusPercent;
    int xp = Math.max(0, Math.round(result));

    if (xp <= 0) {
      return;
    }

    ctx.getWorld().spawnEntity(new ExperienceOrbEntity(
        ctx.getWorld(),
        target.getX(),
        target.getY(),
        target.getZ(),
        xp));
  }
}
