package name.modid.datagen;

import name.modid.datapack.geodes.GeodesDataLoader;
import name.modid.datapack.modifiers.ModifiersDataLoader;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class RecourceHandler {
  public static void initialize() {
    ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ModifiersDataLoader());
    ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new GeodesDataLoader());
  }
}
