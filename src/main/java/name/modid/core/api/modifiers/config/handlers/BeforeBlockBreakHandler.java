package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.BeforeBlockBreakConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierHandler;
import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
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

    Map<EventType, List<GemstoneModifier>> types = modifiers.stream()
        .collect(Collectors.groupingBy(m -> ((BeforeBlockBreakConfig) m.getConfig()).eventType()));

    types.forEach((type, group) -> {
      switch (type) {
        case ON_BLOCK_BREAK_SMELTER -> handleBlockSmelter(group, ctx);
        default -> {
        }
      }
    });
  }

  private void handleBlockSmelter(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (ctx.getTarget() == null) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      BeforeBlockBreakConfig config = (BeforeBlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ctx.getOwner() instanceof LivingEntity owner && ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      List<ItemStack> drops = Block.getDroppedStacks(ctx.getBlockState(), ctx.getWorld(), ctx.getBlockPos(),
          ctx.getWorld().getBlockEntity(ctx.getBlockPos()), owner, owner.getMainHandStack());

      List<ItemStack> smeltableDrops = drops.stream().map(x -> ModifierUtils.getSmeltingResult(ctx.getWorld(), x))
          .filter(x -> !x.isEmpty()).toList();

      if (!smeltableDrops.isEmpty()) {
        for (ItemStack smelted : smeltableDrops) {
          Block.dropStack(ctx.getWorld(), ctx.getBlockPos(), smelted.copy());
        }

        ctx.getWorld().setBlockState(ctx.getBlockPos(), Blocks.AIR.getDefaultState(), 3);

        ItemStack tool = owner.getMainHandStack();
        if (tool.isDamageable()) {
          tool.damage(1, owner, EquipmentSlot.MAINHAND);
        }

        BlockPos pos = ctx.getBlockPos();
        ctx.getWorld().playSound(null, ctx.getBlockPos(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0f,
            1.0f);
        ctx.getWorld().spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 10,
            0.3, 0.3, 0.3, 0.01);
        ctx.getWorld().spawnParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 8, 0.2,
            0.2, 0.2, 0.01);

        ctx.cancel();
        return;
      }
    }
  }
}