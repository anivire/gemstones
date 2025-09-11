package name.modid;

import name.modid.helpers.models.registration.ModelsRegistrationHelper;
import name.modid.helpers.particles.registration.ClientParticlesRegistrationHelper;
import net.fabricmc.api.ClientModInitializer;

public class GemstonesClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ModelsRegistrationHelper.initialize();
    ClientParticlesRegistrationHelper.initialize();
  }
}
