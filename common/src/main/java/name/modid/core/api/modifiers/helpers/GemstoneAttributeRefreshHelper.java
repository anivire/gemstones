package name.modid.core.api.modifiers.helpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class GemstoneAttributeRefreshHelper {
  private GemstoneAttributeRefreshHelper() {
  }

  public static void refreshOnlinePlayers(MinecraftServer server) {
    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
      refreshPlayer(player);
    }
  }

  public static void refreshPlayer(PlayerEntity player) {
    refreshInventory(player.getInventory());
    refreshInventory(player.getEnderChestInventory());
  }

  // Cheap periodic check: only items whose baked modifiers differ from the loaded configs are rewritten
  public static void refreshStaleItems(PlayerEntity player) {
    Inventory inventory = player.getInventory();
    for (int i = 0; i < inventory.size(); i++) {
      GemstoneSlotHelper.refreshAttributesIfStale(inventory.getStack(i));
    }
  }

  private static void refreshInventory(Inventory inventory) {
    for (int i = 0; i < inventory.size(); i++) {
      ItemStack stack = inventory.getStack(i);
      if (!GemstoneSlotHelper.shouldRefreshAttributes(stack)) {
        continue;
      }
      GemstoneSlotHelper.updateSocketsAttributes(stack);
    }
  }
}
