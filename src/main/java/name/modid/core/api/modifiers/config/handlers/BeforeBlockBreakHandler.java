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
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
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
      switch (type.getName()) {
        case "ON_BLOCK_BREAK_SMELTER" -> handleBlockSmelter(group, ctx);
        case "ON_BLOCK_BREAK_ENCHANTER" -> handleEnchanter(group, ctx);
        default -> {
        }
      }
    });
  }

  private void handleBlockSmelter(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner)) {
      return;
    }

    if (ctx.getBlockPos() == null) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      BeforeBlockBreakConfig config = (BeforeBlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.cappedProcChance(chances);

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
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
        ctx.getWorld().playSound(null, ctx.getBlockPos(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5f,
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

  private void handleEnchanter(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof LivingEntity owner)) {
      return;
    }

    if (ctx.getBlockPos() == null || ctx.getBlockState() == null) {
      return;
    }

    List<Double> chances = new ArrayList<>();

    for (GemstoneModifier modifier : modifiers) {
      BeforeBlockBreakConfig config = (BeforeBlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.cappedProcChance(chances);

    if (!ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      return;
    }

    int roll = ctx.getWorld().getRandom().nextInt(100);
    int fortuneLevel;
    if (roll < 70) {
      fortuneLevel = 1;
    } else if (roll < 90) {
      fortuneLevel = 2;
    } else {
      fortuneLevel = 3;
    }

    BlockPos pos = ctx.getBlockPos();
    Block block = ctx.getBlockState().getBlock();
    ItemStack enchantedBlock = new ItemStack(block.asItem());

    if (enchantedBlock.isEmpty()) {
      return;
    }

    Registry<Enchantment> enchRegistry = ctx.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT);
    RegistryEntry<Enchantment> fortuneEntry = enchRegistry.getEntry(Enchantments.FORTUNE).orElseThrow();
    EnchantmentHelper.apply(enchantedBlock, builder -> {
      builder.add(fortuneEntry, fortuneLevel);
    });

    Block.dropStack(ctx.getWorld(), pos, enchantedBlock);
    ctx.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);

    ItemStack tool = owner.getMainHandStack();
    if (tool.isDamageable()) {
      tool.damage(1, owner, EquipmentSlot.MAINHAND);
    }

    ctx.getWorld().playSound(
        null,
        pos,
        SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
        SoundCategory.BLOCKS,
        0.5f,
        1.0f);
    ctx.getWorld().spawnParticles(
        ParticleTypes.ENCHANT,
        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
        20, 0.5, 0.5, 0.5, 0.1);

    ctx.cancel();
  }

}