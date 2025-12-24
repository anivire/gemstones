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

    Map<EventType, List<GemstoneModifier>> types = modifiers.stream()
        .collect(Collectors.groupingBy(m -> ((AfterDeathConfig) m.getConfig()).eventType()));

    types.forEach((type, group) -> {
      switch (type.getName()) {
        case "AFTER_DEATH_DETONATE" -> handleDetonate(group, ctx);
        case "AFTER_DEATH_HARVEST_MARK" -> handleHarvestMark(group, ctx);
        case "AFTER_DEATH_ADDITIONAL_EXP_GAIN" -> handleBonusExpGain(group, ctx);
        default -> {
        }
      }
    });
  }

  private void handleDetonate(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    final float EXPLOSION_POWER = 0.5F;

    if (ctx.getOwner() instanceof LivingEntity owner
        && owner.hasStatusEffect(EffectsRegistry.DETONATE_EFFECT)) {
      ctx.getOwner().getWorld().createExplosion(owner, null, null, owner.getX(), owner.getY(), owner.getZ(),
          EXPLOSION_POWER + owner.getStatusEffect(EffectsRegistry.DETONATE_EFFECT).getAmplifier(), false,
          World.ExplosionSourceType.MOB);
    }
  }

  private void handleHarvestMark(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    final int MIN_ADDITIONAL_XP = 3;
    final int MAX_ADDITIONAL_XP = 5;

    if (ctx.getOwner() instanceof LivingEntity owner
        && owner.hasStatusEffect(EffectsRegistry.HARVEST_MARK_EFFECT)) {
      int stackCount = owner.getStatusEffect(EffectsRegistry.HARVEST_MARK_EFFECT).getAmplifier() + 1;
      int exp = (int) (Math.random() * (MAX_ADDITIONAL_XP - MIN_ADDITIONAL_XP + 1) + MIN_ADDITIONAL_XP);

      for (int i = 0; i < stackCount; i++) {
        ctx.getWorld()
            .spawnEntity(new ExperienceOrbEntity(ctx.getWorld(), owner.getX(), owner.getY(), owner.getZ(), exp));
      }
    }
  }

  private void handleBonusExpGain(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)) {
      return;
    }

    float bonusPercent = 0.0f;
    for (GemstoneModifier modifier : modifiers) {
      AfterDeathConfig config = (AfterDeathConfig) modifier.getConfig();
      bonusPercent += config.values().get(modifier.getRarityType());
    }

    if (bonusPercent <= 0.0f) {
      return;
    }

    int vanillaXp = target.getXpToDrop((net.minecraft.server.world.ServerWorld) ctx.getWorld(), null);
    if (vanillaXp <= 0) {
      return;
    }

    float result = vanillaXp * (1.0f + bonusPercent);
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
