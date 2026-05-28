package name.modid.core.api;

import name.modid.core.api.modifiers.helpers.GemstoneAttributeRefreshHelper;
import name.modid.core.network.DatapackSyncPayload;
import name.modid.datapack.geodes.GeodesDataLoader;
import name.modid.datapack.items.ItemCompatibilityDataLoader;
import name.modid.datapack.modifiers.ModifiersDataLoader;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public final class ClientDatapackSyncHandler {
  private ClientDatapackSyncHandler() {
  }

  public static void initialize() {
    ClientPlayNetworking.registerGlobalReceiver(DatapackSyncPayload.ID, (payload, context) -> {
      context.client().execute(() -> {
        ModifiersDataLoader.applySyncedConfigs(payload.gemstoneConfigs());
        GeodesDataLoader.applySyncedConfigs(payload.geodeConfigs());
        ItemCompatibilityDataLoader.applySyncedConfigs(payload.itemCompatibilityConfigs());

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
          GemstoneAttributeRefreshHelper.refreshPlayer(client.player);
        }
      });
    });
  }
}
