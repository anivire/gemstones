package name.modid.fabric;

import name.modid.GemstonesClient;
import net.fabricmc.api.ClientModInitializer;

public class GemstonesFabricClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    GemstonesClient.initClient();
  }
}
