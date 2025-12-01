package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BlockBreakHandler implements ModifierHandler<ModifierConfig.BlockBreakConfig> {
  private static final int FLAG_NOTIFY_LISTENERS = 1;
  private static final int FLAG_NEIGHBORS = 8;
  private static final int FLAG_FORCE_STATE = 16;
  private static final Set<Identifier> BLOCKED_ITEMS = new HashSet<>();

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
        case ON_BLOCK_BREAK_RANDOM_ITEM_DROP -> handleRandomItemDrop(group, ctx);
        case ON_BLOCK_BREAK_MINER -> handleMiner(group, ctx);
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

  private void handleRandomItemDrop(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (ctx.getBlockPos() == null) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      BlockBreakConfig config = (BlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.combinedProcChance(chances);

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      initializeBlacklist();

      Item randomItem = getRandomAllowedItem();
      if (randomItem == null)
        return;

      ItemStack stack = new ItemStack(randomItem, 1);
      Block.dropStack(ctx.getWorld(), ctx.getBlockPos(), stack);
    }
  }

  private void initializeBlacklist() {
    List<String> blocked = List.of(
        "minecraft:air",
        "minecraft:bedrock",
        "minecraft:barrier",
        "minecraft:command_block",
        "minecraft:chain_command_block",
        "minecraft:repeating_command_block",
        "minecraft:structure_block",
        "minecraft:structure_void",
        "minecraft:debug_stick",
        "minecraft:jigsaw",
        "minecraft:light");

    for (String id : blocked) {
      BLOCKED_ITEMS.add(Identifier.of(id));
    }
  }

  private Item getRandomAllowedItem() {
    Random random = new Random();
    List<Item> allItems = Registries.ITEM.stream()
        .filter(item -> !BLOCKED_ITEMS.contains(Registries.ITEM.getId(item)))
        .toList();

    if (allItems.isEmpty())
      return null;

    return allItems.get(random.nextInt(allItems.size()));
  }

  public void handleMiner(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (ctx.getBlockPos() == null || ctx.getWorld() == null || ctx.getOwner() == null) {
      return;
    }
    if (!(ctx.getWorld() instanceof ServerWorld world))
      return;
    if (!(ctx.getOwner() instanceof ServerPlayerEntity player))
      return;

    BlockPos targetPos = ctx.getBlockPos();
    Direction face = computeHitFace(player, world, targetPos);
    if (face == null)
      return;

    Box bounds = getPlaneBoundsByFace(targetPos, face);

    BlockPos.Mutable m = new BlockPos.Mutable();
    int minX = MathHelper.floor(bounds.minX);
    int minY = MathHelper.floor(bounds.minY);
    int minZ = MathHelper.floor(bounds.minZ);
    int maxX = MathHelper.floor(bounds.maxX);
    int maxY = MathHelper.floor(bounds.maxY);
    int maxZ = MathHelper.floor(bounds.maxZ);

    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        for (int z = minZ; z <= maxZ; z++) {
          m.set(x, y, z);
          if (!world.isInBuildLimit(m))
            continue;

          BlockState state = world.getBlockState(m);
          if (!canBreak(world, m, state))
            continue;

          breakBlockForce(world, player, m, state);
        }
      }
    }

    world.updateNeighborsAlways(targetPos, Blocks.AIR);
  }

  private boolean canBreak(ServerWorld world, BlockPos pos, BlockState state) {
    if (state.isAir())
      return false;
    if (world.getBlockEntity(pos) != null)
      return false;
    float hardness = state.getHardness(world, pos);
    return hardness >= 0.0F;
  }

  private void breakBlockForce(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state) {
    world.breakBlock(pos, true, player);

    if (!world.getBlockState(pos).isAir()) {
      world.setBlockState(
          pos,
          Blocks.AIR.getDefaultState(),
          FLAG_NOTIFY_LISTENERS | FLAG_NEIGHBORS | FLAG_FORCE_STATE);
    }

    player.networkHandler.sendPacket(new BlockUpdateS2CPacket(world, pos));
    world.updateNeighborsAlways(pos, Blocks.AIR);
  }

  private Box getPlaneBoundsByFace(BlockPos center, Direction face) {
    int cx = center.getX();
    int cy = center.getY();
    int cz = center.getZ();

    return switch (face.getAxis()) {
      case Y -> new Box(cx - 1, cy, cz - 1, cx + 1, cy, cz + 1);
      case X -> new Box(cx, cy - 1, cz - 1, cx, cy + 1, cz + 1);
      case Z -> new Box(cx - 1, cy - 1, cz, cx + 1, cy + 1, cz);
    };
  }

  private Direction computeHitFace(ServerPlayerEntity player, ServerWorld world, BlockPos targetPos) {
    final double reach = 5.0;
    final double EPS = 1e-6;

    Vec3d eye = player.getEyePos();
    Vec3d dir = player.getRotationVec(1.0F);

    if (dir.lengthSquared() > 0) {
      dir = dir.normalize();
    } else {
      return null;
    }

    // AABB
    double x1 = targetPos.getX();
    double y1 = targetPos.getY();
    double z1 = targetPos.getZ();
    double x2 = x1 + 1.0;
    double y2 = y1 + 1.0;
    double z2 = z1 + 1.0;

    double tMin = 0.0;
    double tMax = reach;

    // X
    if (Math.abs(dir.x) < EPS) {
      if (eye.x < x1 || eye.x > x2)
        return null;
    } else {
      double tx1 = (x1 - eye.x) / dir.x;
      double tx2 = (x2 - eye.x) / dir.x;
      double tNear = Math.min(tx1, tx2);
      double tFar = Math.max(tx1, tx2);
      tMin = Math.max(tMin, tNear);
      tMax = Math.min(tMax, tFar);
    }

    // Y
    if (Math.abs(dir.y) < EPS) {
      if (eye.y < y1 || eye.y > y2)
        return null;
    } else {
      double ty1 = (y1 - eye.y) / dir.y;
      double ty2 = (y2 - eye.y) / dir.y;
      double tNear = Math.min(ty1, ty2);
      double tFar = Math.max(ty1, ty2);
      tMin = Math.max(tMin, tNear);
      tMax = Math.min(tMax, tFar);
    }

    // Z
    if (Math.abs(dir.z) < EPS) {
      if (eye.z < z1 || eye.z > z2)
        return null;
    } else {
      double tz1 = (z1 - eye.z) / dir.z;
      double tz2 = (z2 - eye.z) / dir.z;
      double tNear = Math.min(tz1, tz2);
      double tFar = Math.max(tz1, tz2);
      tMin = Math.max(tMin, tNear);
      tMax = Math.min(tMax, tFar);
    }

    if (tMin > tMax || tMin < 0.0 || tMin > reach) {
      return null;
    }

    Vec3d hit = eye.add(dir.multiply(tMin));

    double dxMin = Math.abs(hit.x - x1);
    double dxMax = Math.abs(hit.x - x2);
    double dyMin = Math.abs(hit.y - y1);
    double dyMax = Math.abs(hit.y - y2);
    double dzMin = Math.abs(hit.z - z1);
    double dzMax = Math.abs(hit.z - z2);

    double min = dxMin;
    Direction face = Direction.WEST;

    if (dxMax < min) {
      min = dxMax;
      face = Direction.EAST;
    }
    if (dyMin < min) {
      min = dyMin;
      face = Direction.DOWN;
    }
    if (dyMax < min) {
      min = dyMax;
      face = Direction.UP;
    }
    if (dzMin < min) {
      min = dzMin;
      face = Direction.NORTH;
    }
    if (dzMax < min) {
      min = dzMax;
      face = Direction.SOUTH;
    }

    return face;
  }
}