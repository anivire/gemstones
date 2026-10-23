package name.modid.core.utils.amber;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// Amber tools: everything dropped while the player breaks a block (vanilla drops, gem drops,
// 3x3 mining) goes straight to the inventory, experience orbs appear at the player
public final class AmberMagnetHelper {
  // A stack because 3x3 mining breaks blocks from inside another block break
  private static final ThreadLocal<List<PlayerEntity>> ACTIVE_PLAYERS = ThreadLocal.withInitial(ArrayList::new);

  private AmberMagnetHelper() {
  }

  public static void beginBreak(ServerPlayerEntity player) {
    ItemStack tool = player.getMainHandStack();
    boolean hasMagnet = !ModifierGatheringHelper.getModifiersByEventType(tool, EventType.ON_BLOCK_BREAK_MAGNET)
        .isEmpty();
    ACTIVE_PLAYERS.get().add(hasMagnet ? player : null);
  }

  public static void endBreak() {
    List<PlayerEntity> players = ACTIVE_PLAYERS.get();
    if (!players.isEmpty()) {
      players.remove(players.size() - 1);
    }
  }

  private static PlayerEntity getActivePlayer(World world) {
    List<PlayerEntity> players = ACTIVE_PLAYERS.get();
    if (players.isEmpty() || world.isClient()) {
      return null;
    }

    PlayerEntity player = players.get(players.size() - 1);
    return player != null && player.isAlive() && player.getWorld() == world ? player : null;
  }

  // Returns true when the stack was given to the player and must not be dropped in the world
  public static boolean tryCollect(World world, ItemStack stack) {
    PlayerEntity player = getActivePlayer(world);
    if (player == null || stack.isEmpty()) {
      return false;
    }

    ItemStack rest = stack.copy();
    player.getInventory().insertStack(rest);
    if (!rest.isEmpty()) {
      player.dropStack(rest);
    }

    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP,
        SoundCategory.PLAYERS, 0.2F, (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 1.4F + 2.0F);
    return true;
  }

  public static Vec3d getExperiencePosition(World world, Vec3d original) {
    PlayerEntity player = getActivePlayer(world);
    return player != null ? player.getPos() : original;
  }
}
