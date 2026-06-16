package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.core.content.entities.RainArrowEntity;
import name.modid.core.content.registries.EntitiesRegistry;
import name.modid.core.utils.GetRandomBuff;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class HitProjectileHandler implements ModifierHandler<ModifierConfig.HitProjectileConfig> {

  private final Map<String, java.util.function.BiConsumer<List<GemstoneModifier>, ModifierContext>> handlers = Map.of(
      "ON_HIT_EXP_ADDITIONAL_DAMAGE", this::handleAdditionalDamage,
      "ON_HIT_LIGHTNING_BOLT", this::handleLightingBolt,
      "ON_HIT_COPY_ENTITY_DROP", this::handleCopyEntityLoot,
      "ON_HIT_SMALL_FLAT_EXPLOSION", this::handleSmallExplostion,
      "ON_HIT_RANDOM_EFFECT", this::handleRandomEffect,
      "ON_HIT_ARROW_RAIN", this::handleArrowRain,
      "ON_HIT_LIFE_STEAL", this::handleLifesteal);

  private static final List<String> ORDER = List.of(
      "ON_HIT_EXP_ADDITIONAL_DAMAGE",
      "ON_HIT_LIGHTNING_BOLT",
      "ON_HIT_COPY_ENTITY_DROP",
      "ON_HIT_SMALL_FLAT_EXPLOSION",
      "ON_HIT_RANDOM_EFFECT",
      "ON_HIT_ARROW_RAIN",
      "ON_HIT_LIFE_STEAL");

  @Override
  public boolean supports(GemstoneModifier modifier) {
    return modifier.getItemCategory() == ModifierItemCategory.RANGED;
  }

  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty()) {
      return;
    }

    Map<String, List<GemstoneModifier>> grouped = modifiers.stream()
        .collect(Collectors.groupingBy(inst -> ((HitProjectileConfig) inst.getConfig()).eventType().getName()));

    for (String key : ORDER) {
      var group = grouped.get(key);
      if (group == null || group.isEmpty())
        continue;

      var fn = handlers.get(key);
      if (fn != null)
        fn.accept(group, ctx);
    }
  }

  private void handleLifesteal(List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner)) {
      return;
    }

    double chance = ModifierUtils.cappedProcChance(
        modifiers.stream()
            .map(m -> ((HitProjectileConfig) m.getConfig()).values().get(m.getRarityType()))
            .toList());

    if (ModifierUtils.proc(ctx.getWorld(), chance)) {
      owner.heal((float) (ctx.getProjectile().getDamage() * 0.1 + 1.0));

      ctx.getWorld().playSound(null, owner.getBlockPos(),
          SoundEvents.ENTITY_PHANTOM_BITE,
          SoundCategory.PLAYERS,
          0.5f, 0.8f);

      ctx.getWorld().spawnParticles(ParticleTypes.HEART,
          owner.getX(),
          owner.getBodyY(0.5),
          owner.getZ(),
          6,
          0.6, 0.6, 0.6,
          0.4);
    }
  }

  private void handleLightingBolt(List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!ctx.getWorld().isRaining()) {
      return;
    }

    double chance = ModifierUtils.cappedProcChance(
        modifiers.stream()
            .map(m -> ((HitProjectileConfig) m.getConfig()).values().get(m.getRarityType()))
            .toList());

    if (ModifierUtils.proc(ctx.getWorld(), chance)) {
      BlockPos pos = ctx.getTarget() != null
          ? ctx.getTarget().getBlockPos()
          : ctx.getBlockPos();

      if (pos != null) {
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, ctx.getWorld());
        lightning.setPos(pos.getX(), pos.getY(), pos.getZ());

        ctx.getWorld().spawnEntity(lightning);
      }
    }
  }

  private void handleCopyEntityLoot(List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)) {
      return;
    }

    double chance = ModifierUtils.cappedProcChance(
        modifiers.stream()
            .map(m -> ((HitProjectileConfig) m.getConfig()).values().get(m.getRarityType()))
            .toList());

    if (ModifierUtils.proc(ctx.getWorld(), chance)) {
      LootTable lootTable = ctx.getWorld()
          .getServer()
          .getReloadableRegistries()
          .getLootTable(target.getLootTable());

      DamageSource damageSource = ctx.getProjectile() != null
          ? ctx.getWorld().getDamageSources().arrow(ctx.getProjectile(), ctx.getOwner())
          : ctx.getWorld().getDamageSources().generic();

      LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(ctx.getWorld())
          .add(LootContextParameters.THIS_ENTITY, ctx.getTarget())
          .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(ctx.getTarget().getBlockPos()))
          .add(LootContextParameters.DAMAGE_SOURCE, damageSource);

      List<ItemStack> loot = lootTable.generateLoot(builder.build(LootContextTypes.ENTITY));

      for (ItemStack stack : loot) {
        Block.dropStack(ctx.getWorld(), target.getBlockPos(), stack);
      }
    }
  }

  private void handleSmallExplostion(List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)) {
      return;
    }

    double chance = ModifierUtils.cappedProcChance(
        modifiers.stream()
            .map(m -> ((HitProjectileConfig) m.getConfig()).values().get(m.getRarityType()))
            .toList());

    if (ModifierUtils.proc(ctx.getWorld(), chance)) {
      ctx.getWorld().createExplosion(
          null,
          target.getBlockPos().getX(),
          target.getBlockPos().getY(),
          target.getBlockPos().getZ(),
          3.0F,
          ServerWorld.ExplosionSourceType.BLOCK);
    }
  }

  private void handleRandomEffect(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)) {
      return;
    }

    double chance = ModifierUtils.cappedProcChance(
        modifiers.stream()
            .map(m -> ((HitProjectileConfig) m.getConfig()).values().get(m.getRarityType()))
            .toList());

    int duration = 15 * 20;
    int amplifier = ctx.getWorld().getRandom().nextInt(2);

    if (ModifierUtils.proc(ctx.getWorld(), chance)) {
      StatusEffectInstance buff = GetRandomBuff.negative(duration, amplifier);
      target.addStatusEffect(buff);
    }
  }

  private void handleArrowRain(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getTarget() instanceof LivingEntity target)
        || ctx.getProjectile() instanceof RainArrowEntity) {
      return;
    }

    double chance = ModifierUtils.cappedProcChance(
        modifiers.stream()
            .map(m -> ((HitProjectileConfig) m.getConfig()).values().get(m.getRarityType()))
            .toList());

    if (ModifierUtils.proc(ctx.getWorld(), chance)) {
      final int arrows = 8;
      final double height = 16.0;
      final double spreadXZ = 3.0;
      final float baseVelocity = 1.3f;
      final float inaccuracy = 6.0f;
      final int slowSeconds = 1;
      final int slowAmplifier = 1;

      ServerWorld world = (ServerWorld) ctx.getWorld();
      LivingEntity owner = (ctx.getOwner() instanceof LivingEntity le) ? le : null;
      Random rng = world.getRandom();
      Vec3d center = target.getPos();

      for (int i = 0; i < arrows; i++) {
        double dx = (rng.nextDouble() * 2 - 1) * spreadXZ;
        double dz = (rng.nextDouble() * 2 - 1) * spreadXZ;

        double spawnX = center.x + dx;
        double spawnY = center.y + height;
        double spawnZ = center.z + dz;
        RainArrowEntity arrow = EntitiesRegistry.RAIN_ARROW.create(world);

        if (arrow == null) {
          continue;
        }

        arrow.refreshPositionAndAngles(spawnX, spawnY, spawnZ, 0, 0);

        if (owner != null) {
          arrow.setOwner(owner);
        }

        Vec3d targetPoint = target.getPos().add(0.0, target.getHeight() * 0.5, 0.0);
        Vec3d from = new Vec3d(spawnX, spawnY, spawnZ);
        Vec3d dir = targetPoint.subtract(from).normalize();
        float velocity = baseVelocity + (rng.nextFloat() - 0.5f) * 0.3f;

        arrow.setDamage(5.0 + rng.nextDouble() * 1.5);
        arrow.setVelocity(dir.x, dir.y, dir.z, velocity, inaccuracy);
        arrow.setRainSlowness(slowSeconds * 20, slowAmplifier);
        arrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

        world.spawnEntity(arrow);
      }

      world.playSound(
          null,
          target.getBlockPos(),
          SoundEvents.ENTITY_ARROW_SHOOT,
          SoundCategory.PLAYERS,
          0.8f,
          0.9f + world.getRandom().nextFloat() * 0.2f);
    }
  }

  private void handleAdditionalDamage(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof PlayerEntity player)
        || !(ctx.getProjectile() instanceof PersistentProjectileEntity projectile)
        || player.experienceLevel == 0) {
      return;
    }

    int levelOffset = 0;
    float bonusDamagePercent = 0.0f;

    for (GemstoneModifier modifier : modifiers) {
      HitProjectileConfig config = (HitProjectileConfig) modifier.getConfig();
      levelOffset += config.additionalValues().get(modifier.getRarityType()).intValue();
      bonusDamagePercent += config.values().get(modifier.getRarityType());
    }

    float extraDamageMultiplier = 1.0f + (player.experienceLevel / levelOffset) * bonusDamagePercent;
    float newDamage = (float) projectile.getDamage() * extraDamageMultiplier;

    projectile.applyDamageModifier(newDamage);
  }
}
