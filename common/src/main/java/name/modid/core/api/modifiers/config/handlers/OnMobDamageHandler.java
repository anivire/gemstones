package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnMobDamageConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import net.minecraft.entity.LivingEntity;

public class OnMobDamageHandler implements ModifierHandler<ModifierConfig.OnMobDamageConfig> {
  private static final int MISSING_HEALTH_STEPS = 10;

  private final Map<String, java.util.function.BiConsumer<List<GemstoneModifier>, ModifierContext>> handlers = Map.of(
      "PLAYER_BONUS_DAMAGE_MISSING_HEALTH", this::handleBonusDamageMissingHealth);

  private static final List<String> ORDER = List.of(
      "PLAYER_BONUS_DAMAGE_MISSING_HEALTH");

  @Override
  public boolean supports(GemstoneModifier modifier) {
    return modifier.getItemCategory() == ModifierItemCategory.ARMOR;
  }

  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty()) {
      return;
    }

    Map<String, List<GemstoneModifier>> grouped = modifiers.stream()
        .collect(Collectors.groupingBy(inst -> ((OnMobDamageConfig) inst.getConfig()).eventType().getName()));

    for (String key : ORDER) {
      var group = grouped.get(key);
      if (group == null || group.isEmpty())
        continue;

      var fn = handlers.get(key);
      if (fn != null)
        fn.accept(group, ctx);
    }
  }

  // Garnet armor: +X% damage for every 10% of missing health, summed over all gems without a cap.
  // The bonus is part of the hit itself, so it does not reset i-frames and cannot bypass shields
  private void handleBonusDamageMissingHealth(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity playerOwner)) {
      return;
    }

    float maxHealth = playerOwner.getMaxHealth();
    if (maxHealth <= 0.0f) {
      return;
    }

    float missingFraction = Math.max(0.0f, 1.0f - playerOwner.getHealth() / maxHealth);
    int missingSteps = (int) Math.floor(missingFraction * MISSING_HEALTH_STEPS + 1.0E-4);

    if (missingSteps <= 0) {
      return;
    }

    double bonusPercentPerStep = modifiers.stream()
        .mapToDouble(m -> ((OnMobDamageConfig) m.getConfig()).values().get(m.getRarityType()))
        .sum();

    double bonusMultiplier = missingSteps * bonusPercentPerStep;
    if (bonusMultiplier <= 0.0) {
      return;
    }

    ctx.setDamageResult((float) (ctx.getDamageResult() * (1.0 + bonusMultiplier)));
  }
}
