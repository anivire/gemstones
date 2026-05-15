package name.modid.core.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.modid.core.api.ColoredBlockHighlighter.HighlightedBlock;
import name.modid.core.api.modifiers.config.ModifierConfig.BlockBreakConfig;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class MultiMinerClientPreview {
  private static final int PARTICLE_INTERVAL_TICKS = 2;
  private static final int BREAKING_INFO_ID_STRIDE = 32;
  private static final int STALE_BREAKING_INFO_TICKS = 2;
  private static final int HIGHLIGHT_COLOR = 0xFFFFFF;
  private static final Map<Integer, BlockPos> ACTIVE_BREAKING_INFOS = new HashMap<>();
  private static final List<HighlightedBlock> ACTIVE_HIGHLIGHTS = new ArrayList<>();
  private static long lastBreakingInfoUpdateTick = Long.MIN_VALUE;

  private MultiMinerClientPreview() {
  }

  public static boolean hasMultiMiner(PlayerEntity player) {
    for (EquipmentSlot slot : EquipmentSlot.values()) {
      ItemStack stack = player.getEquippedStack(slot);
      if (stack.isEmpty()) {
        continue;
      }

      if (ModifierGatheringHelper.getModifiers(stack, BlockBreakConfig.class).stream()
          .anyMatch(modifier -> modifier.getConfig() instanceof BlockBreakConfig config
              && config.eventType() == EventType.ON_BLOCK_BREAK_MINER)) {
        return true;
      }
    }

    return false;
  }

  public static List<BlockPos> getBreakableBlocks(ClientWorld world, BlockPos center, Direction face,
      boolean includeCenter) {
    List<BlockPos> result = new ArrayList<>();

    int minX = center.getX();
    int maxX = center.getX();
    int minY = center.getY();
    int maxY = center.getY();
    int minZ = center.getZ();
    int maxZ = center.getZ();

    switch (face.getAxis()) {
      case Y -> {
        minX = center.getX() - 1;
        maxX = center.getX() + 1;
        minZ = center.getZ() - 1;
        maxZ = center.getZ() + 1;
      }
      case X -> {
        minY = center.getY() - 1;
        maxY = center.getY() + 1;
        minZ = center.getZ() - 1;
        maxZ = center.getZ() + 1;
      }
      case Z -> {
        minX = center.getX() - 1;
        maxX = center.getX() + 1;
        minY = center.getY() - 1;
        maxY = center.getY() + 1;
      }
    }

    BlockPos.Mutable mutable = new BlockPos.Mutable();
    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        for (int z = minZ; z <= maxZ; z++) {
          mutable.set(x, y, z);
          if (!includeCenter && mutable.equals(center)) {
            continue;
          }

          BlockState state = world.getBlockState(mutable);
          if (canBreak(world, mutable, state)) {
            result.add(mutable.toImmutable());
          }
        }
      }
    }

    return result;
  }

  public static void showMiningFeedback(ClientPlayerEntity player, ClientWorld world, BlockPos center,
      Direction face, int breakingStage) {
    if (!hasMultiMiner(player)) {
      clearBreakingProgress(world);
      return;
    }

    List<BlockPos> blocks = getBreakableBlocks(world, center, face, false);
    for (int i = 0; i < blocks.size(); i++) {
      BlockPos pos = blocks.get(i);
      BlockState state = world.getBlockState(pos);
      showBreakingProgress(player, world, pos, i, breakingStage);

      if (world.getTime() % PARTICLE_INTERVAL_TICKS == 0) {
        spawnMiningParticle(world, pos, state, face, i);
      }
    }

    clearRemovedBreakingInfos(player, world, blocks.size());
    lastBreakingInfoUpdateTick = world.getTime();
  }

  public static void updateTargetHighlights(ClientPlayerEntity player, ClientWorld world, HitResult hit) {
    if (!hasMultiMiner(player) || !(hit instanceof BlockHitResult blockHit)) {
      clearHighlights();
      return;
    }

    List<BlockPos> blocks = getBreakableBlocks(world, blockHit.getBlockPos(), blockHit.getSide(), true);

    ACTIVE_HIGHLIGHTS.clear();
    for (BlockPos pos : blocks) {
      ACTIVE_HIGHLIGHTS.add(new HighlightedBlock(pos, HIGHLIGHT_COLOR));
    }
  }

  public static boolean hasActiveHighlights() {
    return !ACTIVE_HIGHLIGHTS.isEmpty();
  }

  public static List<HighlightedBlock> getActiveHighlights() {
    return List.copyOf(ACTIVE_HIGHLIGHTS);
  }

  public static void clearStaleBreakingProgress(ClientWorld world) {
    if (!ACTIVE_BREAKING_INFOS.isEmpty()
        && world.getTime() - lastBreakingInfoUpdateTick > STALE_BREAKING_INFO_TICKS) {
      clearBreakingProgress(world);
    }
  }

  public static void clearBreakingProgress(ClientWorld world) {
    ACTIVE_BREAKING_INFOS.forEach((entityId, pos) -> world.setBlockBreakingInfo(entityId, pos, -1));
    ACTIVE_BREAKING_INFOS.clear();
    lastBreakingInfoUpdateTick = Long.MIN_VALUE;
  }

  public static void clearHighlights() {
    ACTIVE_HIGHLIGHTS.clear();
  }

  public static boolean canBreak(ClientWorld world, BlockPos pos, BlockState state) {
    if (state.isAir()) {
      return false;
    }
    if (world.getBlockEntity(pos) != null) {
      return false;
    }
    return state.getHardness(world, pos) >= 0.0F;
  }

  private static void spawnMiningParticle(ClientWorld world, BlockPos pos, BlockState state, Direction face, int index) {
    double seed = world.getTime() + index * 13.0D;
    double x = pos.getX() + 0.5D + Math.sin(seed * 0.71D) * 0.32D;
    double y = pos.getY() + 0.5D + Math.cos(seed * 0.53D) * 0.32D;
    double z = pos.getZ() + 0.5D + Math.sin(seed * 0.37D) * 0.32D;

    x += face.getOffsetX() * 0.52D;
    y += face.getOffsetY() * 0.52D;
    z += face.getOffsetZ() * 0.52D;

    world.addParticle(
        new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
        x,
        y,
        z,
        face.getOffsetX() * 0.03D,
        face.getOffsetY() * 0.03D,
        face.getOffsetZ() * 0.03D);
  }

  private static void showBreakingProgress(ClientPlayerEntity player, ClientWorld world, BlockPos pos, int index,
      int breakingStage) {
    int entityId = getBreakingInfoId(player, index);
    int stage = Math.max(0, Math.min(9, breakingStage));
    BlockPos previousPos = ACTIVE_BREAKING_INFOS.get(entityId);

    if (previousPos != null && !previousPos.equals(pos)) {
      world.setBlockBreakingInfo(entityId, previousPos, -1);
    }

    ACTIVE_BREAKING_INFOS.put(entityId, pos);
    world.setBlockBreakingInfo(entityId, pos, stage);
  }

  private static void clearRemovedBreakingInfos(ClientPlayerEntity player, ClientWorld world, int visibleBlockCount) {
    List<Integer> staleIds = ACTIVE_BREAKING_INFOS.keySet().stream()
        .filter(entityId -> !isCurrentBreakingInfoId(player, entityId, visibleBlockCount))
        .toList();

    for (int entityId : staleIds) {
      BlockPos pos = ACTIVE_BREAKING_INFOS.remove(entityId);
      if (pos != null) {
        world.setBlockBreakingInfo(entityId, pos, -1);
      }
    }
  }

  private static boolean isCurrentBreakingInfoId(ClientPlayerEntity player, int entityId, int visibleBlockCount) {
    int firstId = getBreakingInfoId(player, 0);
    int lastId = getBreakingInfoId(player, visibleBlockCount - 1);
    return entityId >= firstId && entityId <= lastId;
  }

  private static int getBreakingInfoId(ClientPlayerEntity player, int index) {
    return Integer.MIN_VALUE + (player.getId() + 1) * BREAKING_INFO_ID_STRIDE + index;
  }

}
