package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.AfterDeathConfig;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.EffectsRegistry;
import name.modid.core.utils.damage.DamageSourceHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class EventAfterDeath {
  private static final float DETONATE_BASE_POWER = 2.5F;
  private static final float DETONATE_POWER_PER_LEVEL = 0.5F;
  private static final int MIN_HARVEST_MARK_XP = 3;
  private static final int MAX_HARVEST_MARK_XP = 5;

  // Detonate hurts only mobs: no block damage, no damage to players, pets, villagers or dropped loot
  private static final ExplosionBehavior DETONATE_BEHAVIOR = new ExplosionBehavior() {
    @Override
    public boolean canDestroyBlock(Explosion explosion, net.minecraft.world.BlockView world,
        net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state, float power) {
      return false;
    }

    @Override
    public boolean shouldDamage(Explosion explosion, Entity entity) {
      return canDetonateHit(entity);
    }

    @Override
    public float getKnockbackModifier(Entity entity) {
      return canDetonateHit(entity) ? super.getKnockbackModifier(entity) : 0.0F;
    }
  };

  public static void setupEvent(LivingEntity entity, DamageSource damageSource) {
    if (!(entity.getWorld() instanceof ServerWorld serverWorld)) {
      return;
    }

    // Damage over time and bonus damage have no attacker, the kill goes to the player who hit the entity
    Entity killer = DamageSourceHelper.getKiller(entity, damageSource);

    detonateIfMarked(serverWorld, entity, killer);

    if (!(killer instanceof ServerPlayerEntity player)) {
      return;
    }

    dropHarvestMarkExperience(serverWorld, entity);

    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        player,
        AfterDeathConfig.class);

    if (modifiers.isEmpty()) {
      return;
    }

    ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(player)
        .withTarget(entity);
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctxBuilder.build());
  }

  private static void detonateIfMarked(ServerWorld world, LivingEntity entity, Entity killer) {
    if (!entity.hasStatusEffect(EffectsRegistry.detonateEntry())) {
      return;
    }

    DamageSource explosionSource = world.getDamageSources().explosion(entity,
        killer instanceof LivingEntity ? killer : null);

    world.createExplosion(
        entity,
        explosionSource,
        DETONATE_BEHAVIOR,
        entity.getX(),
        entity.getY(),
        entity.getZ(),
        DETONATE_BASE_POWER + DETONATE_POWER_PER_LEVEL * entity.getStatusEffect(EffectsRegistry.detonateEntry()).getAmplifier(),
        false,
        World.ExplosionSourceType.NONE);
  }

  private static void dropHarvestMarkExperience(ServerWorld world, LivingEntity entity) {
    if (!entity.hasStatusEffect(EffectsRegistry.harvestMarkEntry())) {
      return;
    }

    int stackCount = entity.getStatusEffect(EffectsRegistry.harvestMarkEntry()).getAmplifier() + 1;

    for (int i = 0; i < stackCount; i++) {
      int exp = MIN_HARVEST_MARK_XP + world.getRandom().nextInt(MAX_HARVEST_MARK_XP - MIN_HARVEST_MARK_XP + 1);
      world.spawnEntity(new ExperienceOrbEntity(world, entity.getX(), entity.getY(), entity.getZ(), exp));
    }
  }

  private static boolean canDetonateHit(Entity entity) {
    if (!(entity instanceof LivingEntity)
        || entity instanceof PlayerEntity
        || entity instanceof ArmorStandEntity
        || entity instanceof MerchantEntity) {
      return false;
    }

    return !(entity instanceof Tameable tameable && tameable.getOwnerUuid() != null);
  }
}
