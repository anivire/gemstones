package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.BlockBreakConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.content.items.registries.ItemsRegistry;
import name.modid.core.content.registries.TagsRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class BlockBreakHandler implements ModifierHandler<ModifierConfig.BlockBreakConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    Map<EventType, List<GemstoneModifier>> types = modifiers.stream()
        .collect(Collectors.groupingBy(m -> ((BlockBreakConfig) m.getConfig()).eventType()));

    types.forEach((type, group) -> {
      switch (type) {
        case ON_BLOCK_BREAK_HEAL -> handleHeal(group, ctx);
        case ON_BLOCK_BREAK_REGENERATE_BLOCK -> handleRegenerateBlock(group, ctx);
        case ON_BLOCK_BREAK_ADDITIONAL_GOLD_DROP -> handleAdditionalGoldDrop(group, ctx);
        case ON_BLOCK_BREAK_INCREASE_GEODES_DROP -> handleIncreaseGeodesDrop(group, ctx);
        case ON_BLOCK_BREAK_EXTRA_HEALTH -> handleExtraHealth(group, ctx);
        default -> {
        }
      }

    });
  }

  private void handleHeal(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof PlayerEntity player)) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    float totalHealAmount = 0.0f;

    for (GemstoneModifier modifier : modifiers) {
      BlockBreakConfig config = (BlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
      totalHealAmount += config.additionValues().get(modifier.getRarityType()).floatValue();
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      player.heal(totalHealAmount);
      // TODO: add particles and sound
    }
  }

  private void handleRegenerateBlock(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (ctx.getBlockPos() == null)
      return;

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      BlockBreakConfig config = (BlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)
        && ctx.getWorld() instanceof ServerWorld serverWorld) {
      BlockPos pos = ctx.getBlockPos();
      serverWorld.setBlockState(pos, ctx.getBlockState(), Block.FORCE_STATE);

      serverWorld.spawnParticles(ParticleTypes.CLOUD,
          pos.getX() + 0.5,
          pos.getY() + 0.5,
          pos.getZ() + 0.5,
          20, 0.3, 0.3, 0.3, 0.2);
      serverWorld.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
          SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
  }

  private void handleAdditionalGoldDrop(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (ctx.getBlockPos() == null)
      return;

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      BlockBreakConfig config = (BlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      ItemStack goldIngot = new ItemStack(Items.GOLD_INGOT);
      goldIngot.setCount(1);

      ItemStack goldNugget = new ItemStack(Items.GOLD_NUGGET);
      goldNugget.setCount(ctx.getWorld().getRandom().nextBetween(3, 6));

      Block.dropStack(ctx.getWorld(), ctx.getBlockPos(),
          ctx.getWorld().getRandom().nextDouble() >= 0.6F ? goldIngot : goldNugget);
    }
  }

  private void handleIncreaseGeodesDrop(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!ctx.getBlockState().isIn(TagsRegistry.ALL_ORES)
        && ctx.getBlockPos() == null) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      BlockBreakConfig config = (BlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      ItemStack geode = ctx.getBlockState().isIn(TagsRegistry.DEEPSLATE_ORES)
          ? new ItemStack(ItemsRegistry.DEEPSLATE_GEODE)
          : new ItemStack(ItemsRegistry.STONE_GEODE);

      Block.dropStack(ctx.getWorld(), ctx.getBlockPos(), geode);
    }
  }

  private void handleExtraHealth(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof PlayerEntity player)) {
      return;
    }

    int totalMaxStacks = 0;
    List<Double> chances = new ArrayList<>();

    for (GemstoneModifier modifier : modifiers) {
      BlockBreakConfig config = (BlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
      totalMaxStacks += config.additionValues().get(modifier.getRarityType()).intValue();
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (totalMaxStacks > 0
        && ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      double healthPerProc = 1.0;
      int buffDuration = totalMaxStacks < 3 ? 1800
          : totalMaxStacks <= 5 ? 3600
              : 4800;

      float currentAbsorption = player.getAbsorptionAmount();
      player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, buffDuration,
          (int) totalMaxStacks - 1, false, false, true));
      player.setAbsorptionAmount(currentAbsorption + (float) healthPerProc);
    }
  }
}