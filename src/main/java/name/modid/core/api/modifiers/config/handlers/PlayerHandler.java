package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.PlayerConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public class PlayerHandler implements ModifierHandler<ModifierConfig.PlayerConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    EventType type = ((PlayerConfig) modifiers.get(0).getConfig()).eventType();

    switch (type) {
      case PLAYER_WITHER_GUARD -> handleWitherGuard(modifiers, ctx);
      case PLAYER_PROJECTILE_IMMUNE -> handleProjectileImmune(modifiers, ctx);
      default -> {
      }
    }
  }

  private void handleWitherGuard(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (ctx.getOwner() == null) {
      return;
    }

    ctx.setActionResult(ActionResult.SUCCESS);
  }

  private void handleProjectileImmune(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target) || ctx.getProjectile() == null) {
      ctx.setIsHurtable(true);
      return;
    }

    float health = target.getHealth();
    float maxHealth = target.getMaxHealth();
    float healthPercentage = health / maxHealth;

    float healthPercentageCap = 0.0F;
    for (GemstoneModifier modifier : modifiers) {
      PlayerConfig config = (PlayerConfig) modifier.getConfig();
      healthPercentageCap += Math.abs(config.values().get(modifier.getRarityType()));
    }

    if (healthPercentage < healthPercentageCap) {
      ctx.setIsHurtable(false);
    } else {
      ctx.setIsHurtable(true);
    }
  }
}
