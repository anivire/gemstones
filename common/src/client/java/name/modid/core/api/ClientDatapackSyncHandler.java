package name.modid.core.api;

import name.modid.core.api.modifiers.helpers.GemstoneAttributeRefreshHelper;
import name.modid.core.network.DatapackSyncPayload;
import name.modid.datapack.drops.DropsDataLoader;
import name.modid.datapack.geodes.GeodesDataLoader;
import name.modid.datapack.items.ItemCompatibilityDataLoader;
import name.modid.datapack.modifiers.ModifiersDataLoader;
import name.modid.datapack.sockets.SocketSettingsDataLoader;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;

public final class ClientDatapackSyncHandler {
  private ClientDatapackSyncHandler() {
  }

  public static void initialize() {
    NetworkManager.registerReceiver(NetworkManager.s2c(), DatapackSyncPayload.ID, DatapackSyncPayload.CODEC,
        (payload, context) -> context.queue(() -> {
        ModifiersDataLoader.applySyncedConfigs(payload.gemstoneConfigs());
        GeodesDataLoader.applySyncedConfigs(payload.geodeConfigs());
        ItemCompatibilityDataLoader.applySyncedConfigs(payload.itemCompatibilityConfigs());
        DropsDataLoader.applySyncedConfigs(payload.dropsConfigs());
        SocketSettingsDataLoader.applySyncedConfigs(payload.socketSettingsConfigs());

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
          GemstoneAttributeRefreshHelper.refreshPlayer(client.player);
        }
    }));
  }
}
