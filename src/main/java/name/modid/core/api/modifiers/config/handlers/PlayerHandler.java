package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.PlayerConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.utils.GetRandomBuff;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.random.Random;

public class PlayerHandler implements ModifierHandler<ModifierConfig.PlayerConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    EventType type = ((PlayerConfig) modifiers.get(0).getConfig()).eventType();

    switch (type) {
      case PLAYER_WITHER_GUARD -> handleWitherGuard(modifiers, ctx);
      case PLAYER_PROJECTILE_IMMUNE -> handleProjectileImmune(modifiers, ctx);
      case PLAYER_RANDOM_EFFECT -> handleRandomEffect(modifiers, ctx);
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

    var isHaveWitherGuard = false;
    for (GemstoneModifier modifier : modifiers) {
      PlayerConfig config = (PlayerConfig) modifier.getConfig();
      if (config.eventType() == EventType.PLAYER_WITHER_GUARD) {
        isHaveWitherGuard = true;
      }
    }

    if (!isHaveWitherGuard) {
      ctx.setActionResult(ActionResult.FAIL);
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

  private void handleRandomEffect(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner))
      return;

    Random random = ctx.getWorld().getRandom();
    int amplifier = random.nextInt(2);
    int combinedDuration = 0;
    double combinedChance = 0.0;

    for (GemstoneModifier modifier : modifiers) {
      PlayerConfig config = (PlayerConfig) modifier.getConfig();
      combinedDuration += config.values().get(modifier.getRarityType());
      combinedChance += config.additionValues().get(modifier.getRarityType());
    }

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      StatusEffectInstance buff = GetRandomBuff.positive(combinedDuration, amplifier);
      owner.addStatusEffect(buff);
    }
  }
}
