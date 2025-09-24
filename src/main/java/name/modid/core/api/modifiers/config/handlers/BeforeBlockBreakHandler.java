package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.BeforeBlockBreakConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class BeforeBlockBreakHandler implements ModifierHandler<ModifierConfig.BeforeBlockBreakConfig> {
  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    EventType type = ((BeforeBlockBreakConfig) modifiers.get(0).getConfig()).eventType();

    switch (type) {
      case ON_BLOCK_BREAK_SMELTER -> handleBlockSmelter(modifiers, ctx);
      default -> {
      }
    }
  }

  private void handleBlockSmelter(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (ctx.getTarget() == null) {
      return;
    }

    double combinedChance = 0.0;
    for (GemstoneModifier modifier : modifiers) {
      HitProjectileConfig config = (HitProjectileConfig) modifier.getConfig();
      combinedChance += config.chance().get(modifier.getRarityType());
    }

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      List<ItemStack> drops = Block.getDroppedStacks(
          ctx.getBlockState(),
          ctx.getWorld(),
          ctx.getBlockPos(),
          ctx.getWorld().getBlockEntity(ctx.getBlockPos()),
          ctx.getOwner(),
          ctx.getOwner().getMainHandStack());

      List<ItemStack> smeltableDrops = drops.stream()
          .map(x -> Utils.getSmeltingResult(ctx.getWorld(), x))
          .filter(x -> !x.isEmpty())
          .toList();

      if (!smeltableDrops.isEmpty()) {
        for (ItemStack smelted : smeltableDrops) {
          Block.dropStack(ctx.getWorld(), ctx.getBlockPos(), smelted.copy());
        }

        ctx.getWorld().setBlockState(ctx.getBlockPos(), Blocks.AIR.getDefaultState(), 3);

        ItemStack tool = ctx.getOwner().getMainHandStack();
        if (tool.isDamageable()) {
          tool.damage(1, ctx.getOwner(), EquipmentSlot.MAINHAND);
        }

        BlockPos pos = ctx.getBlockPos();
        ctx.getWorld().playSound(
            null,
            ctx.getBlockPos(),
            SoundEvents.BLOCK_LAVA_EXTINGUISH,
            SoundCategory.BLOCKS,
            1.0f,
            1.0f);
        ctx.getWorld().spawnParticles(
            ParticleTypes.SMOKE,
            pos.getX() + 0.5,
            pos.getY() + 1.0,
            pos.getZ() + 0.5,
            10,
            0.3,
            0.3,
            0.3,
            0.01);
        ctx.getWorld().spawnParticles(
            ParticleTypes.FLAME,
            pos.getX() + 0.5,
            pos.getY() + 0.8,
            pos.getZ() + 0.5,
            8,
            0.2,
            0.2,
            0.2,
            0.01);

        ctx.cancel();
        return;
      }
    }
  }
}