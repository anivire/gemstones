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
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.core.content.registries.TagsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;

public class BeforeBlockBreakHandler implements ModifierHandler<ModifierConfig.BeforeBlockBreakConfig> {
  // Opal gamble: losing the drop is half as likely as doubling it
  public static final double GAMBLE_LOSS_RATIO = 0.5;

  @Override
  public boolean supports(GemstoneModifier modifier) {
    return modifier.getItemCategory() == ModifierItemCategory.TOOLS;
  }

  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    Map<EventType, List<GemstoneModifier>> types = modifiers.stream()
        .collect(Collectors.groupingBy(m -> ((BeforeBlockBreakConfig) m.getConfig()).eventType()));

    types.forEach((type, group) -> {
      if (ctx.isCancelled()) {
        return;
      }

      switch (type.getName()) {
        case "ON_BLOCK_BREAK_SMELTER" -> handleBlockSmelter(group, ctx);
        case "ON_BLOCK_BREAK_ENCHANTER" -> handleEnchanter(group, ctx);
        case "ON_BLOCK_BREAK_GAMBLE" -> handleGamble(group, ctx);
        default -> {
        }
      }
    });
  }

  private void handleBlockSmelter(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof PlayerEntity player)
        || ctx.getBlockPos() == null
        || ctx.getBlockState() == null) {
      return;
    }

    ServerWorld world = ctx.getWorld();
    BlockPos pos = ctx.getBlockPos();
    BlockState state = ctx.getBlockState();
    ItemStack tool = player.getMainHandStack();

    // Without the right tool vanilla drops nothing, the smelter must not bypass that
    if (tool.isEmpty() || hasEnchantment(world, tool, Enchantments.SILK_TOUCH) || !player.canHarvest(state)) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      BeforeBlockBreakConfig config = (BeforeBlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.cappedProcChance(chances);

    if (!ModifierUtils.proc(world, combinedChance)) {
      return;
    }

    List<ItemStack> drops = Block.getDroppedStacks(state, world, pos, world.getBlockEntity(pos), player, tool);

    // Drops that cannot be smelted (geodes, gemstones from loot tables) are kept as they are
    List<ItemStack> finalDrops = new ArrayList<>();
    boolean smeltedAny = false;
    for (ItemStack drop : drops) {
      ItemStack smelted = ModifierUtils.getSmeltingResult(world, drop);
      if (smelted.isEmpty()) {
        finalDrops.add(drop);
        continue;
      }

      smelted.setCount(smelted.getCount() * drop.getCount());
      finalDrops.add(smelted);
      smeltedAny = true;
    }

    if (!smeltedAny) {
      return;
    }

    breakWithDrops(world, player, pos, state, tool, finalDrops, true);

    world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5f,
        1.0f);
    world.spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 10,
        0.3, 0.3, 0.3, 0.01);
    world.spawnParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 8, 0.2,
        0.2, 0.2, 0.01);

    ctx.cancel();
  }

  private void handleEnchanter(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof PlayerEntity player)
        || ctx.getBlockPos() == null
        || ctx.getBlockState() == null) {
      return;
    }

    ServerWorld world = ctx.getWorld();
    BlockPos pos = ctx.getBlockPos();
    BlockState state = ctx.getBlockState();
    ItemStack tool = player.getMainHandStack();

    if (tool.isEmpty() || !player.canHarvest(state)) {
      return;
    }

    List<Double> chances = new ArrayList<>();

    for (GemstoneModifier modifier : modifiers) {
      BeforeBlockBreakConfig config = (BeforeBlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.cappedProcChance(chances);

    if (!ModifierUtils.proc(world, combinedChance)) {
      return;
    }

    Registry<Enchantment> enchRegistry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
    RegistryEntry<Enchantment> silkTouchEntry = enchRegistry.getEntry(Enchantments.SILK_TOUCH).orElse(null);
    RegistryEntry<Enchantment> fortuneEntry = enchRegistry.getEntry(Enchantments.FORTUNE).orElse(null);
    if (silkTouchEntry == null || fortuneEntry == null) {
      return;
    }

    // Drops are taken from the loot table with a silk touch copy of the tool: shulker contents,
    // banner patterns, bees and pot sherds are kept, unobtainable blocks (budding amethyst, spawners)
    // still drop nothing
    ItemStack silkTouchTool = tool.copy();
    EnchantmentHelper.apply(silkTouchTool, builder -> builder.add(silkTouchEntry, 1));

    List<ItemStack> drops = Block.getDroppedStacks(state, world, pos, world.getBlockEntity(pos), player,
        silkTouchTool);
    if (drops.isEmpty()) {
      return;
    }

    int roll = world.getRandom().nextInt(100);
    int fortuneLevel;
    if (roll < 70) {
      fortuneLevel = 1;
    } else if (roll < 90) {
      fortuneLevel = 2;
    } else {
      fortuneLevel = 3;
    }

    for (ItemStack drop : drops) {
      if (drop.isOf(state.getBlock().asItem())) {
        EnchantmentHelper.apply(drop, builder -> builder.add(fortuneEntry, fortuneLevel));
      }
    }

    breakWithDrops(world, player, pos, state, tool, drops, false);

    world.playSound(
        null,
        pos,
        SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
        SoundCategory.BLOCKS,
        0.5f,
        1.0f);
    world.spawnParticles(
        ParticleTypes.ENCHANT,
        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
        20, 0.5, 0.5, 0.5, 0.1);

    ctx.cancel();
  }

  private void handleGamble(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof PlayerEntity player)
        || ctx.getBlockPos() == null
        || ctx.getBlockState() == null) {
      return;
    }

    ServerWorld world = ctx.getWorld();
    BlockPos pos = ctx.getBlockPos();
    BlockState state = ctx.getBlockState();
    ItemStack tool = player.getMainHandStack();

    if (!state.isIn(TagsRegistry.ALL_ORES) && !state.isIn(TagsRegistry.CONVENTIONAL_ORES)) {
      return;
    }
    if (tool.isEmpty() || hasEnchantment(world, tool, Enchantments.SILK_TOUCH) || !player.canHarvest(state)) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      BeforeBlockBreakConfig config = (BeforeBlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double winChance = ModifierUtils.cappedProcChance(chances);
    double roll = world.getRandom().nextDouble();
    boolean won = roll < winChance;
    boolean lost = !won && roll < winChance * (1.0 + GAMBLE_LOSS_RATIO);

    if (!won && !lost) {
      return;
    }

    List<ItemStack> drops = Block.getDroppedStacks(state, world, pos, world.getBlockEntity(pos), player, tool);
    if (drops.isEmpty()) {
      return;
    }

    List<ItemStack> finalDrops = new ArrayList<>();
    if (won) {
      for (ItemStack drop : drops) {
        finalDrops.add(drop.copy());
        finalDrops.add(drop.copy());
      }
    }

    // Experience is not part of the bet
    breakWithDrops(world, player, pos, state, tool, finalDrops, true);

    double x = pos.getX() + 0.5;
    double y = pos.getY() + 0.5;
    double z = pos.getZ() + 0.5;
    if (won) {
      world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0f, 1.2f);
      world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 10, 0.35, 0.35, 0.35, 0.0);
    } else {
      world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.6f, 1.3f);
      world.spawnParticles(ParticleTypes.SMOKE, x, y, z, 12, 0.3, 0.3, 0.3, 0.01);
    }

    ctx.cancel();
  }

  // Same steps as a vanilla break, but with custom drops
  private static void breakWithDrops(
      ServerWorld world,
      PlayerEntity player,
      BlockPos pos,
      BlockState state,
      ItemStack tool,
      List<ItemStack> drops,
      boolean dropExperience) {
    ItemStack toolCopy = tool.copy();

    state.getBlock().onBreak(world, pos, state, player);
    if (world.removeBlock(pos, false)) {
      state.getBlock().onBroken(world, pos, state);
    }

    for (ItemStack drop : drops) {
      Block.dropStack(world, pos, drop);
    }

    if (dropExperience) {
      state.onStacksDropped(world, pos, toolCopy, true);
    }

    player.incrementStat(Stats.MINED.getOrCreateStat(state.getBlock()));
    player.addExhaustion(0.005F);
    tool.postMine(world, state, pos, player);
  }

  private static boolean hasEnchantment(ServerWorld world,
      ItemStack tool,
      net.minecraft.registry.RegistryKey<Enchantment> enchantment) {
    return world.getRegistryManager().get(RegistryKeys.ENCHANTMENT)
        .getEntry(enchantment)
        .map(entry -> EnchantmentHelper.getLevel(entry, tool) > 0)
        .orElse(false);
  }
}
