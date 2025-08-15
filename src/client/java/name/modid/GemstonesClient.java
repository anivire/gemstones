package name.modid;

import name.modid.helpers.models.ModelsRegistrationHelper;
import name.modid.helpers.particles.ClientParticlesRegistrationHelper;
import net.fabricmc.api.ClientModInitializer;

public class GemstonesClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ModelsRegistrationHelper.initialize();
    ClientParticlesRegistrationHelper.initialize();
  }
}
