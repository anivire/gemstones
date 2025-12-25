package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.OnDamageConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class OnDamageHandler implements ModifierHandler<ModifierConfig.OnDamageConfig> {
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
        .collect(Collectors.groupingBy(inst -> ((OnDamageConfig) inst.getConfig()).eventType().getName()));

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
    if (!(ctx.getOwner() instanceof LivingEntity attacker) ||
        !(ctx.getTarget() instanceof LivingEntity target)) {
      return;
    }

    DamageSource bonusSource = ctx.getWorld().getDamageSources().generic();
    float missingHealth = Math.max(0, attacker.getMaxHealth() - attacker.getHealth());
    int missingHearts = (int) Math.ceil(missingHealth / 2.0f);

    if (missingHearts <= 0) {
      return;
    }

    double bonusPercentPerHeart = modifiers.stream()
        .mapToDouble(m -> ((OnDamageConfig) m.getConfig()).values().get(m.getRarityType()))
        .sum();

    double bonusMultiplier = missingHearts * (bonusPercentPerHeart / 100.0);
    float bonusDamage = (float) (ctx.getBaseDamageTaken() * bonusMultiplier);

    if (bonusDamage <= 0 || !target.isAlive()) {
      return;
    }

    if (!target.isInvulnerableTo(bonusSource) && target.isAlive()) {
      int originalHurtTime = target.hurtTime;

      target.hurtTime = 0;
      target.timeUntilRegen = 0;
      target.hurtTime = Math.max(1, originalHurtTime / 2);
      target.damage(bonusSource, bonusDamage);

      ctx.getWorld().playSound(null, target.getBlockPos(),
          SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
          SoundCategory.PLAYERS,
          0.8f, 1.2f + (missingHearts * 0.1f));

      double x = target.getX();
      double y = target.getBodyY(0.5);
      double z = target.getZ();

      if (ctx.getWorld() instanceof ServerWorld serverWorld) {
        serverWorld.spawnParticles(
            ParticleTypes.ANGRY_VILLAGER,
            x, y, z,
            (int) (missingHearts * 2),
            0.5, 0.5, 0.5,
            0.1);
      }
    }
  }
}