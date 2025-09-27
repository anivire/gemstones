package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnFirstHitConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.entity.LivingEntity;

public class OnFirstHitHandler
    implements ModifierHandler<ModifierConfig.OnFirstHitConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    EventType type = ((OnFirstHitConfig) modifiers.get(0).getConfig()).eventType();

    switch (type) {
      case ON_FIRST_HIT_ADDITIONAL_DAMAGE -> handleAdditionalDamage(modifiers, ctx);
      default -> {
      }
    }
  }

  private void handleAdditionalDamage(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (ctx.getOwner() == null
        || ctx.getTarget() == null) {
      return;
    }

    if (ctx.getTarget() instanceof LivingEntity target &&
        target.getHealth() == target.getMaxHealth()) {
      float additionalDamagePercent = 0.0F;
      for (GemstoneModifier modifier : modifiers) {
        OnFirstHitConfig config = (OnFirstHitConfig) modifier.getConfig();
        additionalDamagePercent += config.values().get(modifier.getRarityType());
      }

      ctx.setDamageResult(ctx.getBaseDamageTaken() * (1 + additionalDamagePercent));
    }
  }
}