package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.AfterDeathConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class AfterDeathHandler implements ModifierHandler<ModifierConfig.AfterDeathConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    EventType type = ((AfterDeathConfig) modifiers.get(0).getConfig()).eventType();

    switch (type) {
      case AFTER_DEATH_DETONATE -> handleDetonate(modifiers, ctx);
      case AFTER_DEATH_HARVEST_MARK -> handleHarvestMark(modifiers, ctx);
      default -> {
      }
    }
  }

  private void handleDetonate(List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    final float EXPLOSION_POWER = 0.5F;

    if (ctx.getOwner() instanceof LivingEntity owner
        && owner.hasStatusEffect(EffectsRegistry.DETONATE_EFFECT)) {
      ctx.getOwner().getWorld().createExplosion(
          owner,
          null,
          null,
          owner.getX(),
          owner.getY(),
          owner.getZ(),
          EXPLOSION_POWER + owner.getStatusEffect(EffectsRegistry.DETONATE_EFFECT).getAmplifier(),
          false,
          World.ExplosionSourceType.MOB);
    }
  }

  private void handleHarvestMark(List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    final int MIN_ADDITIONAL_XP = 3;
    final int MAX_ADDITIONAL_XP = 5;

    if (ctx.getOwner() instanceof LivingEntity owner
        && owner.hasStatusEffect(EffectsRegistry.HARVEST_MARK_EFFECT)) {
      int stackCount = owner.getStatusEffect(EffectsRegistry.HARVEST_MARK_EFFECT).getAmplifier() + 1;
      int exp = (int) (Math.random() * (MAX_ADDITIONAL_XP - MIN_ADDITIONAL_XP + 1) + MIN_ADDITIONAL_XP);

      for (int i = 0; i < stackCount; i++) {
        ctx.getWorld().spawnEntity(new ExperienceOrbEntity(
            ctx.getWorld(),
            owner.getX(),
            owner.getY(),
            owner.getZ(),
            exp));
      }
    }
  }
}
