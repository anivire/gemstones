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
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class OnDamageHandler implements ModifierHandler<ModifierConfig.OnDamageConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    Map<EventType, List<GemstoneModifier>> types = modifiers.stream()
        .collect(Collectors.groupingBy(m -> ((OnDamageConfig) m.getConfig()).eventType()));

    types.forEach((type, group) -> {
      switch (type) {
        case PLAYER_BONUS_DAMAGE_MISSING_HEALTH -> handleBonusDamageMissingHealth(group, ctx);
        default -> {
        }
      }
    });
  }

  private void handleBonusDamageMissingHealth(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity attacker) ||
        !(ctx.getTarget() instanceof LivingEntity target)) {
      return;
    }

    float currentHealth = attacker.getHealth();
    float maxHealth = attacker.getMaxHealth();
    float missingHealth = Math.max(0, maxHealth - currentHealth);
    int missingHearts = (int) Math.ceil(missingHealth / 2.0f);

    if (missingHearts <= 0) {
      return;
    }

    double bonusPercentPerHeart = 0.0;
    for (GemstoneModifier modifier : modifiers) {
      OnDamageConfig config = (OnDamageConfig) modifier.getConfig();
      bonusPercentPerHeart += config.values().get(modifier.getRarityType());
    }

    double bonusMultiplier = missingHearts * (bonusPercentPerHeart / 100.0);
    float bonusDamage = (float) (ctx.getBaseDamageTaken() * bonusMultiplier);

    if (bonusDamage <= 0 || !target.isAlive()) {
      return;
    }

    World world = ctx.getWorld();
    DamageSources damageSources = world.getDamageSources();

    DamageSource bonusSource = damageSources.generic();

    if (!target.isInvulnerableTo(bonusSource)) {
      int originalHurtTime = target.hurtTime;
      target.hurtTime = 0;
      target.timeUntilRegen = 0;

      target.damage(bonusSource, bonusDamage);

      target.hurtTime = Math.max(1, originalHurtTime / 2);
    }

    if (target.isAlive()) {
      world.playSound(null, target.getBlockPos(),
          SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
          SoundCategory.PLAYERS,
          0.8f, 1.2f + (missingHearts * 0.1f));

      if (world instanceof ServerWorld serverWorld) {
        double x = target.getX();
        double y = target.getBodyY(0.5);
        double z = target.getZ();

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