package name.modid.core.api.models;

public class ModelsRegistry {
  public static void initialize() {
    BowModel.register();
    CrossbowModel.register();
  }
}
