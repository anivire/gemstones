package name.modid.helpers.models.registration;

import name.modid.helpers.models.BowModel;
import name.modid.helpers.models.CrossbowModel;

public class ModelsRegistrationHelper {
  public static void initialize() {
    BowModel.register();
    CrossbowModel.register();
  }
}
