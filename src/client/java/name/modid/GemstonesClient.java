package name.modid;

import name.modid.core.api.models.ModelsRegistry;
import name.modid.core.api.particles.ClientParticlesRegistry;
import net.fabricmc.api.ClientModInitializer;

public class GemstonesClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ModelsRegistry.initialize();
    ClientParticlesRegistry.initialize();
  }
}
