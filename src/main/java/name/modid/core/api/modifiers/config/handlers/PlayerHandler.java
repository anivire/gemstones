package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.PlayerConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.content.registries.EffectsRegistry;
import name.modid.core.utils.GetRandomBuff;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.random.Random;

public class PlayerHandler implements ModifierHandler<ModifierConfig.PlayerConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    Map<EventType, List<GemstoneModifier>> types = modifiers.stream()
        .collect(Collectors.groupingBy(m -> ((PlayerConfig) m.getConfig()).eventType()));

    types.forEach((type, group) -> {
      switch (type) {
        case PLAYER_WITHER_GUARD -> handleWitherGuard(group, ctx);
        case PLAYER_PROJECTILE_IMMUNE -> handleProjectileImmune(group, ctx);
        case PLAYER_RANDOM_EFFECT -> handleRandomEffect(group, ctx);
        case PLAYER_SAVE_LETHAL -> handleSaveLethal(group, ctx);
        default -> {
        }
      }
    });
  }

  private void handleWitherGuard(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (ctx.getOwner() == null) {
      return;
    }

    if (!modifiers.isEmpty()) {
      ctx.setActionResult(ActionResult.SUCCESS);
    } else {
      ctx.setActionResult(ActionResult.FAIL);
    }
  }

  private void handleProjectileImmune(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner)
        || !(ctx.getTarget() instanceof LivingEntity target)
        || ctx.getProjectile() == null) {
      ctx.setIsHurtable(true);
      return;
    }

    if (owner.getUuid().equals(target.getUuid()))
      return;

    float health = target.getHealth();
    float maxHealth = target.getMaxHealth();
    float healthPercentage = health / maxHealth;

    float healthPercentageCap = 0.0F;
    for (GemstoneModifier modifier : modifiers) {
      if (modifier.getConfig() instanceof PlayerConfig m
          && m.eventType() == EventType.PLAYER_PROJECTILE_IMMUNE) {
        PlayerConfig config = (PlayerConfig) modifier.getConfig();
        healthPercentageCap += Math.abs(config.values().get(modifier.getRarityType()));
      }
    }

    if (healthPercentage < healthPercentageCap) {
      ctx.setIsHurtable(false);
    } else {
      ctx.setIsHurtable(true);
    }
  }

  private void handleRandomEffect(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner)
        || !(ctx.getTarget() instanceof LivingEntity target))
      return;

    if (owner.getUuid().equals(target.getUuid()))
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
      StatusEffectInstance buff = GetRandomBuff.positive(combinedDuration * 20, amplifier);
      owner.addStatusEffect(buff);
    }
  }

  private void handleSaveLethal(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity owner))
      return;

    if (owner.isDead() || owner.isRemoved() || owner.isSpectator())
      return;

    if (owner.hasStatusEffect(EffectsRegistry.LETHAL_WEAKNESS_EFFECT))
      return;

    int amplifier = 1;
    int totalDurationSeconds = 0;
    double hpThresholdBonus = 0.0;

    for (GemstoneModifier modifier : modifiers) {
      PlayerConfig config = (PlayerConfig) modifier.getConfig();
      totalDurationSeconds += config.values().get(modifier.getRarityType());
      hpThresholdBonus += config.additionValues().get(modifier.getRarityType());
    }

    float hpThreshold = (float) (0.0 + hpThresholdBonus);
    int buffDurationTicks = Math.max(0, totalDurationSeconds) * 20;

    if (buffDurationTicks <= 0)
      return;

    if (owner.getHealth() > hpThreshold)
      return;

    owner.getWorld().playSound(
        null,
        owner.getBlockPos(),
        net.minecraft.sound.SoundEvents.ITEM_TOTEM_USE,
        net.minecraft.sound.SoundCategory.PLAYERS,
        1.0f,
        1.0f);

    if (owner.getWorld() instanceof ServerWorld sw) {
      double x = owner.getX();
      double y = owner.getBodyY(0.5);
      double z = owner.getZ();
      sw.spawnParticles(
          net.minecraft.particle.ParticleTypes.TOTEM_OF_UNDYING,
          x, y, z,
          50,
          0.5, 0.8, 0.5,
          0.1);
      sw.spawnParticles(
          net.minecraft.particle.ParticleTypes.GLOW,
          x, y, z,
          20,
          0.3, 0.6, 0.3,
          0.02);
    }

    owner.addStatusEffect(new StatusEffectInstance(
        StatusEffects.REGENERATION,
        buffDurationTicks,
        amplifier,
        false,
        true,
        true));

    owner.addStatusEffect(new StatusEffectInstance(
        StatusEffects.RESISTANCE,
        buffDurationTicks,
        0,
        false,
        true,
        true));

    owner.addStatusEffect(new StatusEffectInstance(
        EffectsRegistry.LETHAL_WEAKNESS_EFFECT,
        3 * 60 * 20,
        0,
        false,
        true,
        true));

    owner.addStatusEffect(new StatusEffectInstance(
        StatusEffects.ABSORPTION,
        buffDurationTicks,
        1,
        false,
        true,
        true));
  }
}
