package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnFirstHitConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import net.minecraft.entity.LivingEntity;

public class OnFirstHitHandler implements ModifierHandler<ModifierConfig.OnFirstHitConfig> {

  private final Map<String, java.util.function.BiConsumer<List<GemstoneModifier>, ModifierContext>> handlers = Map.of(
      "ON_FIRST_HIT_ADDITIONAL_DAMAGE", this::handleAdditionalDamage);

  private static final List<String> ORDER = List.of(
      "ON_FIRST_HIT_ADDITIONAL_DAMAGE");

  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty()) {
      return;
    }

    Map<String, List<GemstoneModifier>> grouped = modifiers.stream()
        .collect(Collectors.groupingBy(inst -> ((OnFirstHitConfig) inst.getConfig()).eventType().getName()));

    for (String key : ORDER) {
      var group = grouped.get(key);
      if (group == null || group.isEmpty())
        continue;

      var fn = handlers.get(key);
      if (fn != null)
        fn.accept(group, ctx);
    }
  }

  // NOTE: don't capped
  private void handleAdditionalDamage(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)
        || ctx.getOwner() == null) {
      return;
    }

    if (target.getHealth() == target.getMaxHealth()) {
      float additionalDamagePercent = (float) modifiers.stream()
          .mapToDouble(m -> ((OnFirstHitConfig) m.getConfig()).values().get(m.getRarityType()))
          .sum();

      ctx.setDamageResult(ctx.getBaseDamageTaken() * (1 + additionalDamagePercent));
    }
  }
}