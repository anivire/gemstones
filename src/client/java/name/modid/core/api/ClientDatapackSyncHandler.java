package name.modid.core.api;

import name.modid.core.network.DatapackSyncPayload;
import name.modid.datapack.geodes.GeodesDataLoader;
import name.modid.datapack.modifiers.ModifiersDataLoader;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientDatapackSyncHandler {
  private ClientDatapackSyncHandler() {
  }

  public static void initialize() {
    ClientPlayNetworking.registerGlobalReceiver(DatapackSyncPayload.ID, (payload, context) -> {
      ModifiersDataLoader.applySyncedConfigs(payload.gemstoneConfigs());
      GeodesDataLoader.applySyncedConfigs(payload.geodeConfigs());
    });
  }
}
