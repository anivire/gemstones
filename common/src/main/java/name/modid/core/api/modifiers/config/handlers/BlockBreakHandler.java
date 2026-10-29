package name.modid.core.api.modifiers.config.handlers;

import java.util.ArrayDeque;
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
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.core.content.registries.TagsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
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
import net.minecraft.world.WorldEvents;

public class BlockBreakHandler implements ModifierHandler<ModifierConfig.BlockBreakConfig> {
  private static final int UPDATE_FLAGS = Block.NOTIFY_ALL;
  private static final Set<Identifier> BLOCKED_ITEMS = new HashSet<>();
  // Set while the miner or chain lightning break extra blocks, so those breaks do
  // not chain further
  private static final ThreadLocal<Boolean> BREAKING_EXTRA_BLOCKS = ThreadLocal.withInitial(() -> false);
  private static final List<PendingRegeneration> PENDING_REGENERATIONS = new ArrayList<>();
  private static final float MIN_SATURATION_HARDNESS = 1.0F;
  private static final float MAX_EXTRA_ABSORPTION = 10.0F; // Five hearts
  private static final int EXTRA_ABSORPTION_DURATION_TICKS = 90 * 20;

  private record PendingRegeneration(ServerWorld world, BlockPos pos, BlockState state, int ticksRemaining) {
  }

  // Sapphire chain lightning jumps to the next block of the same type every few
  // ticks instead of breaking them at once
  private static final int CHAIN_STEP_TICKS = 3;
  private static final List<ChainLightning> ACTIVE_CHAINS = new ArrayList<>();

  private static final class ChainLightning {
    private final ServerWorld world;
    private final ServerPlayerEntity player;
    private final ItemStack tool;
    private final Block block;
    private final List<BlockPos> vein;
    private int next = 0;
    private int cooldown = CHAIN_STEP_TICKS;

    private ChainLightning(ServerWorld world, ServerPlayerEntity player, ItemStack tool, Block block,
        List<BlockPos> vein) {
      this.world = world;
      this.player = player;
      this.tool = tool;
      this.block = block;
      this.vein = vein;
    }
  }

  @Override
  public boolean supports(GemstoneModifier modifier) {
    return modifier.getItemCategory() == ModifierItemCategory.TOOLS;
  }

  @Override
  public void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (modifiers.isEmpty())
      return;

    Map<EventType, List<GemstoneModifier>> types = modifiers.stream()
        .collect(Collectors.groupingBy(m -> ((BlockBreakConfig) m.getConfig()).eventType()));

    types.forEach((type, group) -> {
      switch (type.getName()) {
        case "ON_BLOCK_BREAK_HEAL" -> handleHeal(group, ctx);
        case "ON_BLOCK_BREAK_SATURATION" -> handleSaturation(group, ctx);
        case "ON_BLOCK_BREAK_REGENERATE_BLOCK" -> handleRegenerateBlock(group, ctx);
        case "ON_BLOCK_BREAK_ADDITIONAL_GOLD_DROP" -> handleAdditionalGoldDrop(group, ctx);
        case "ON_BLOCK_BREAK_EXTRA_HEALTH" -> handleExtraHealth(group, ctx);
        case "ON_BLOCK_BREAK_RANDOM_ITEM_DROP" -> handleRandomItemDrop(group, ctx);
        case "ON_BLOCK_BREAK_MINER" -> {
          if (!BREAKING_EXTRA_BLOCKS.get()) {
            handleMiner(group, ctx);
          }
        }
        case "ON_BLOCK_BREAK_CHAIN_LIGHTNING" -> {
          if (!BREAKING_EXTRA_BLOCKS.get()) {
            handleChainLightning(group, ctx);
          }
        }
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
      totalHealAmount += config.additionalValues().get(modifier.getRarityType()).floatValue();
    }

    double combinedChance = ModifierUtils.cappedProcChance(chances);

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      float healthBefore = player.getHealth();
      player.heal(totalHealAmount);

      if (player.getHealth() > healthBefore && ctx.getWorld() instanceof ServerWorld serverWorld) {
        serverWorld.spawnParticles(ParticleTypes.HEART,
            player.getX(),
            player.getY() + 1.0,
            player.getZ(),
            4,
            0.35,
            0.4,
            0.35,
            0.02);
        serverWorld.playSound(null,
            player.getBlockPos(),
            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
            SoundCategory.PLAYERS,
            0.45f,
            1.35f);
      }
    }
  }

  // Feeds the player while mining without replacing healing in combat. Instantly
  // broken blocks
  // (torches, grass) do not count, so the effect cannot be farmed by placing and
  // breaking them
  private void handleSaturation(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof PlayerEntity player)
        || ctx.getBlockPos() == null
        || ctx.getBlockState() == null
        || ctx.getBlockState().getHardness(ctx.getWorld(), ctx.getBlockPos()) < MIN_SATURATION_HARDNESS) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    float saturation = 0.0f;

    for (GemstoneModifier modifier : modifiers) {
      BlockBreakConfig config = (BlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
      saturation = Math.max(saturation, config.additionalValues().get(modifier.getRarityType()).floatValue());
    }

    if (saturation <= 0.0f || !ModifierUtils.proc(ctx.getWorld(), ModifierUtils.cappedProcChance(chances))) {
      return;
    }

    player.getHungerManager().add(0, saturation);
    ctx.getWorld().playSound(null,
        player.getBlockPos(),
        SoundEvents.ENTITY_GENERIC_EAT,
        SoundCategory.PLAYERS,
        0.35f,
        1.4f);
  }

  private void handleRegenerateBlock(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (ctx.getBlockPos() == null)
      return;
    if (!ctx.getBlockState().isIn(TagsRegistry.ALL_ORES)) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      BlockBreakConfig config = (BlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.cappedProcChance(chances);

    // Ancient debris is too valuable to regenerate as often as regular ores
    if (ctx.getBlockState().isOf(Blocks.ANCIENT_DEBRIS)) {
      combinedChance *= 0.5;
    }

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)
        && ctx.getWorld() instanceof ServerWorld serverWorld) {
      PENDING_REGENERATIONS.add(new PendingRegeneration(
          serverWorld,
          ctx.getBlockPos().toImmutable(),
          ctx.getBlockState(),
          1));
    }
  }

  public static void tickPendingRegenerations() {
    List<PendingRegeneration> remaining = new ArrayList<>();
    for (PendingRegeneration regeneration : PENDING_REGENERATIONS) {
      if (regeneration.ticksRemaining() > 0) {
        remaining.add(new PendingRegeneration(
            regeneration.world(),
            regeneration.pos(),
            regeneration.state(),
            regeneration.ticksRemaining() - 1));
        continue;
      }

      restoreBlock(regeneration.world(), regeneration.pos(), regeneration.state());
    }
    PENDING_REGENERATIONS.clear();
    PENDING_REGENERATIONS.addAll(remaining);
  }

  private static void restoreBlock(ServerWorld world, BlockPos pos, BlockState state) {
    if (!world.getBlockState(pos).isAir()) {
      return;
    }

    world.setBlockState(pos, state, UPDATE_FLAGS);
    world.getChunkManager().markForUpdate(pos);
    world.spawnParticles(ParticleTypes.CLOUD,
        pos.getX() + 0.5,
        pos.getY() + 0.5,
        pos.getZ() + 0.5,
        20, 0.3, 0.3, 0.3, 0.2);
    world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
        SoundCategory.BLOCKS, 1.0f, 1.0f);
  }

  private void handleAdditionalGoldDrop(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    // Only blocks in the ore tags can yield gold nuggets.
    if (ctx.getBlockPos() == null || ctx.getBlockState() == null)
      return;
    BlockState state = ctx.getBlockState();
    if (!isOre(state))
      return;

    List<Double> chances = new ArrayList<>();
    for (GemstoneModifier modifier : modifiers) {
      BlockBreakConfig config = (BlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
    }

    double combinedChance = ModifierUtils.cappedProcChance(chances);

    if (ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      ItemStack goldNugget = new ItemStack(Items.GOLD_NUGGET, ctx.getWorld().getRandom().nextBetween(1, 3));
      Block.dropStack(ctx.getWorld(), ctx.getBlockPos(), goldNugget);
    }
  }

  private void handleExtraHealth(
      List<GemstoneModifier> modifiers,
      ModifierContext ctx) {
    if (!(ctx.getOwner() instanceof PlayerEntity player)) {
      return;
    }

    float absorptionPerProc = 0.0f;
    List<Double> chances = new ArrayList<>();

    for (GemstoneModifier modifier : modifiers) {
      BlockBreakConfig config = (BlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
      absorptionPerProc = Math.max(absorptionPerProc,
          config.additionalValues().get(modifier.getRarityType()).floatValue());
    }

    double combinedChance = ModifierUtils.cappedProcChance(chances);

    if (absorptionPerProc > 0.0f
        && ModifierUtils.proc(ctx.getWorld(), combinedChance)) {
      float currentAbsorption = player.getAbsorptionAmount();
      // Absorption III removes up to 12 health when it expires, clearing the fixed
      // 10-health Garnet shield even after repeated procs.
      player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,
          EXTRA_ABSORPTION_DURATION_TICKS, 2, false, false, true));
      player.setAbsorptionAmount(Math.max(currentAbsorption,
          Math.min(MAX_EXTRA_ABSORPTION, currentAbsorption + absorptionPerProc)));

      if (player.getAbsorptionAmount() > currentAbsorption && ctx.getWorld() instanceof ServerWorld serverWorld) {
        serverWorld.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
            player.getX(),
            player.getY() + 1.0,
            player.getZ(),
            12,
            0.45,
            0.5,
            0.45,
            0.08);
        serverWorld.playSound(null,
            player.getBlockPos(),
            SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
            SoundCategory.PLAYERS,
            0.45f,
            1.45f);
      }
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

    double combinedChance = ModifierUtils.cappedProcChance(chances);

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

  private void handleChainLightning(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (ctx.getBlockPos() == null || ctx.getBlockState() == null) {
      return;
    }
    if (!(ctx.getWorld() instanceof ServerWorld world)
        || !(ctx.getOwner() instanceof ServerPlayerEntity player)) {
      return;
    }

    BlockState origin = ctx.getBlockState();
    if (player.getMainHandStack().getMiningSpeedMultiplier(origin) <= 1.0F) {
      return;
    }

    List<Double> chances = new ArrayList<>();
    int maxBlocks = 0;
    for (GemstoneModifier modifier : modifiers) {
      BlockBreakConfig config = (BlockBreakConfig) modifier.getConfig();
      chances.add(config.values().get(modifier.getRarityType()));
      maxBlocks = Math.max(maxBlocks, config.additionalValues().get(modifier.getRarityType()).intValue());
    }

    if (maxBlocks <= 0 || !ModifierUtils.proc(world, ModifierUtils.cappedProcChance(chances))) {
      return;
    }

    List<BlockPos> vein = findVein(world, ctx.getBlockPos(), origin.getBlock(), maxBlocks);
    if (vein.isEmpty()) {
      return;
    }

    world.playSound(null, ctx.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS,
        0.5f, 1.8f);

    ACTIVE_CHAINS.add(new ChainLightning(world, player, player.getMainHandStack(), origin.getBlock(), vein));
  }

  public static void tickChainLightnings() {
    ACTIVE_CHAINS.removeIf(chain -> !stepChain(chain));
  }

  // Returns false once the chain is over
  private static boolean stepChain(ChainLightning chain) {
    ServerPlayerEntity player = chain.player;

    // Leaving, dying, changing dimension or switching the tool ends the chain
    if (player.isRemoved() || !player.isAlive() || player.getServerWorld() != chain.world
        || player.getMainHandStack() != chain.tool) {
      return false;
    }

    if (--chain.cooldown > 0) {
      return true;
    }
    chain.cooldown = CHAIN_STEP_TICKS;

    BlockPos pos = chain.vein.get(chain.next++);
    BlockState state = chain.world.getBlockState(pos);

    // The blocks may have changed meanwhile, or the tool can no longer harvest them
    if (!state.isOf(chain.block) || !player.canHarvest(state)) {
      return false;
    }

    ItemStack tool = chain.tool;
    if (tool.isDamageable() && tool.getDamage() >= tool.getMaxDamage() - 1) {
      return false;
    }

    int damage = tool.getDamage();

    chain.world.playSound(null, pos, SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE, SoundCategory.PLAYERS,
        0.4f, 1.4f + chain.world.getRandom().nextFloat() * 0.6f);
    breakBlockAsPlayer(chain.world, player, pos, state);
    if (tool.isDamageable()) {
      tool.setDamage(damage);
    }

    return chain.next < chain.vein.size();
  }

  // Breadth-first walk over the touching blocks of the same type, nearest first
  private static List<BlockPos> findVein(ServerWorld world, BlockPos start, Block block, int limit) {
    List<BlockPos> vein = new ArrayList<>();
    Set<BlockPos> visited = new HashSet<>();
    ArrayDeque<BlockPos> queue = new ArrayDeque<>();
    visited.add(start);
    queue.add(start);

    while (!queue.isEmpty() && vein.size() < limit) {
      BlockPos current = queue.poll();

      for (BlockPos neighbor : BlockPos.iterate(current.add(-1, -1, -1), current.add(1, 1, 1))) {
        if (vein.size() >= limit) {
          break;
        }

        BlockPos pos = neighbor.toImmutable();
        if (visited.add(pos) && world.getBlockState(pos).isOf(block)) {
          vein.add(pos);
          queue.add(pos);
        }
      }
    }

    return vein;
  }

  private static boolean isOre(BlockState state) {
    return state.isIn(TagsRegistry.ALL_ORES) || state.isIn(TagsRegistry.CONVENTIONAL_ORES);
  }

  public void handleMiner(List<GemstoneModifier> modifiers, ModifierContext ctx) {
    if (ctx.getBlockPos() == null || ctx.getWorld() == null || ctx.getOwner() == null) {
      return;
    }
    if (!(ctx.getWorld() instanceof ServerWorld world))
      return;
    if (!(ctx.getOwner() instanceof ServerPlayerEntity player))
      return;
    if (player.isSneaking())
      return;

    BlockPos targetPos = ctx.getBlockPos();
    Direction face = computeHitFace(player, world, targetPos);
    if (face == null)
      return;

    int radius = getMinerRadius(modifiers);
    Box bounds = getPlaneBoundsByFace(targetPos, face, radius);
    float centerHardness = ctx.getBlockState() != null
        ? ctx.getBlockState().getHardness(world, targetPos)
        : 0.0F;
    ItemStack tool = player.getMainHandStack();

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
          if (m.equals(targetPos))
            continue;
          if (!world.isInBuildLimit(m))
            continue;

          BlockState state = world.getBlockState(m);
          if (!canBreak(world, m, state, centerHardness) || !isRightTool(player, tool, state))
            continue;

          breakBlockAsPlayer(world, player, m, state);
        }
      }
    }

    world.updateNeighborsAlways(targetPos, Blocks.AIR);
  }

  private boolean canBreak(ServerWorld world, BlockPos pos, BlockState state, float centerHardness) {
    if (state.isAir())
      return false;
    if (state.getBlock() instanceof FluidBlock)
      return false;
    if (world.getBlockEntity(pos) != null)
      return false;
    float hardness = state.getHardness(world, pos);
    if (hardness < 0.0F)
      return false;

    // Mining stone must not wipe torches, rails, redstone or crops around the
    // target
    return hardness > 0.0F || centerHardness <= 0.0F;
  }

  // A pickaxe must not dig out the dirt or wood around the stone, and a block the tool
  // is too weak for (obsidian with an iron pickaxe) would break without a drop
  private static boolean isRightTool(ServerPlayerEntity player, ItemStack tool, BlockState state) {
    return tool.getMiningSpeedMultiplier(state) > 1.0F && player.canHarvest(state);
  }

  private static void breakBlockAsPlayer(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state) {
    BlockPos immutablePos = pos.toImmutable();
    boolean breakingExtraBlocks = BREAKING_EXTRA_BLOCKS.get();

    BREAKING_EXTRA_BLOCKS.set(true);
    try {
      player.interactionManager.tryBreakBlock(immutablePos);
    } finally {
      BREAKING_EXTRA_BLOCKS.set(breakingExtraBlocks);
    }

    // Vanilla sends the break particles to everyone except the breaking player,
    // whose client
    // normally spawns them itself. A server-side break never reaches that client,
    // so send them here
    if (!world.getBlockState(immutablePos).isOf(state.getBlock())) {
      player.networkHandler.send(new WorldEventS2CPacket(
          WorldEvents.BLOCK_BROKEN, immutablePos, Block.getRawIdFromState(state), false), null);
    }

    world.updateListeners(immutablePos, state, world.getBlockState(immutablePos), UPDATE_FLAGS);
    world.getChunkManager().markForUpdate(immutablePos);
    world.updateNeighborsAlways(immutablePos, Blocks.AIR);
  }

  private int getMinerRadius(List<GemstoneModifier> modifiers) {
    double radius = 1.0;
    for (GemstoneModifier modifier : modifiers) {
      BlockBreakConfig config = (BlockBreakConfig) modifier.getConfig();
      radius = Math.max(radius, config.additionalValues().get(modifier.getRarityType()));
    }

    return getMinerRadius(radius);
  }

  public static int getMinerRadius(double radius) {
    return Math.max(1, Math.round((float) radius));
  }

  private Box getPlaneBoundsByFace(BlockPos center, Direction face, int radius) {
    int cx = center.getX();
    int cy = center.getY();
    int cz = center.getZ();

    return switch (face.getAxis()) {
      case Y -> new Box(cx - radius, cy, cz - radius, cx + radius, cy, cz + radius);
      case X -> new Box(cx, cy - radius, cz - radius, cx, cy + radius, cz + radius);
      case Z -> new Box(cx - radius, cy - radius, cz, cx + radius, cy + radius, cz);
    };
  }

  private Direction computeHitFace(ServerPlayerEntity player, ServerWorld world, BlockPos targetPos) {
    final double reach = player.getBlockInteractionRange() + 1.0;
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
