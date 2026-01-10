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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class OnMobDamageHandler implements ModifierHandler<ModifierConfig.OnMobDamageConfig> {
  private final Map<String, java.util.function.BiConsumer<List<GemstoneModifier>, ModifierContext>> handlers = Map.of(
      "PLAYER_BONUS_DAMAGE_MISSING_HEALTH", this::handleBonusDamageMissingHealth);

  private static final List<String> ORDER = List.of(
      "PLAYER_BONUS_DAMAGE_MISSING_HEALTH");

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

  // NOTE: don't capped
  private void handleBonusDamageMissingHealth(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity playerOwner) ||
        !(ctx.getTarget() instanceof LivingEntity targetedMob)) {
      return;
    }

    DamageSource bonusSource = ctx.getWorld().getDamageSources().generic();
    float missingHealth = Math.max(0, playerOwner.getMaxHealth() - playerOwner.getHealth());
    int missingHearts = (int) Math.ceil(missingHealth / 2.0f);

    if (missingHearts <= 0) {
      return;
    }

    double bonusPercentPerHeart = modifiers.stream()
        .mapToDouble(m -> ((OnMobDamageConfig) m.getConfig()).values().get(m.getRarityType()))
        .sum();

    double bonusMultiplier = missingHearts * bonusPercentPerHeart;
    float bonusDamage = (float) (ctx.getBaseDamageTaken() * bonusMultiplier);

    if (bonusDamage <= 0 || !targetedMob.isAlive()) {
      return;
    }

    if (!targetedMob.isInvulnerableTo(bonusSource) && targetedMob.isAlive()) {
      int originalHurtTime = targetedMob.hurtTime;

      targetedMob.hurtTime = 0;
      targetedMob.timeUntilRegen = 0;
      targetedMob.hurtTime = Math.max(1, originalHurtTime / 2);
      targetedMob.damage(bonusSource, bonusDamage);

      ctx.getWorld().playSound(null, targetedMob.getBlockPos(),
          SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
          SoundCategory.PLAYERS,
          0.8f, 1.2f + (missingHearts * 0.1f));

      if (ctx.getWorld() instanceof ServerWorld serverWorld) {
        serverWorld.spawnParticles(
            ParticleTypes.ENCHANTED_HIT,
            targetedMob.getX(),
            targetedMob.getBodyY(0.5),
            targetedMob.getZ(),
            5,
            0.5, 0.5, 0.5,
            0.1);
      }
    }
  }
}