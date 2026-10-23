package name.modid.fabric;

import name.modid.Gemstones;
import name.modid.core.network.NetworkHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class GemstonesFabric implements ModInitializer {
  @Override
  public void onInitialize() {
    Gemstones.init();

    // After /reload the clients and the baked item attributes must follow the new datapacks
    ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
      if (success) {
        NetworkHandler.resyncAll(server);
      }
    });
  }
}
