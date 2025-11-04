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
import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.utils.GetRandomBuff;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
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
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    Map<EventType, List<GemstoneModifier>> types = modifiers.stream()
        .collect(Collectors.groupingBy(m -> ((HitProjectileConfig) m.getConfig()).eventType()));

    types.forEach((type, group) -> {
      switch (type) {
        case ON_HIT_LIFE_STEAL -> handleLifesteal(group, ctx);
        case ON_HIT_LIGHTNING_BOLT -> handleLightingBolt(group, ctx);
        case ON_HIT_COPY_ENTITY_DROP -> handleCopyEntityLoot(group, ctx);
        case ON_HIT_SMALL_FLAT_EXPLOSION -> handleSmallExplostion(group, ctx);
        case ON_HIT_RANDOM_EFFECT -> handleRandomEffect(group, ctx);
        default -> {
        }
      }
    });
  }

  private void handleLifesteal(List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (ctx.getTarget() == null) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      HitProjectileConfig config = (HitProjectileConfig) modifier.getConfig();
      chances.add(config.chance().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ctx.getOwner() instanceof LivingEntity owner
        && ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
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

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      HitProjectileConfig config = (HitProjectileConfig) modifier.getConfig();
      chances.add(config.chance().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
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
    if (ctx.getTarget() == null) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      HitProjectileConfig config = (HitProjectileConfig) modifier.getConfig();
      chances.add(config.chance().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ctx.getTarget() instanceof LivingEntity target
        && ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
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
    if (ctx.getTarget() == null || ctx.getBlockPos() == null)
      return;

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      HitProjectileConfig config = (HitProjectileConfig) modifier.getConfig();
      chances.add(config.chance().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      BlockPos pos = null;

      if (ctx.getTarget() instanceof LivingEntity target) {
        pos = target.getBlockPos();
      } else if (ctx.getBlockPos() != null) {
        pos = ctx.getBlockPos();
      }

      if (pos != null) {
        ctx.getWorld().createExplosion(
            null,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            3.0F,
            ServerWorld.ExplosionSourceType.BLOCK);
      }
    }
  }

  private void handleRandomEffect(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (ctx.getTarget() == null) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      HitProjectileConfig config = (HitProjectileConfig) modifier.getConfig();
      chances.add(config.chance().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    Random random = ctx.getWorld().getRandom();
    int duration = 15 * 20;
    int amplifier = random.nextInt(2);

    if (ctx.getTarget() instanceof LivingEntity target
        && ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      StatusEffectInstance buff = GetRandomBuff.negative(duration, amplifier);
      target.addStatusEffect(buff);
    }
  }
}