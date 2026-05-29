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
  }

  private static void refreshInventory(Inventory inventory) {
    for (int i = 0; i < inventory.size(); i++) {
      ItemStack stack = inventory.getStack(i);
      GemstoneSlotHelper.updateSocketsAttributes(stack);
    }
  }
}
