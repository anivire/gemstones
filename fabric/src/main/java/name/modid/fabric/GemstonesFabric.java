package name.modid.fabric;

import name.modid.Gemstones;
import net.fabricmc.api.ModInitializer;

public class GemstonesFabric implements ModInitializer {
  @Override
  public void onInitialize() {
    Gemstones.init();
  }
}
