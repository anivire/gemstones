package name.modid.datagen;

import name.modid.datapack.drops.DropsDataLoader;
import name.modid.datapack.geodes.GeodesDataLoader;
import name.modid.datapack.items.ItemCompatibilityDataLoader;
import name.modid.datapack.modifiers.ModifiersDataLoader;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class ResourceHandler {
  public static void initialize() {
    ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ModifiersDataLoader());
    ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new GeodesDataLoader());
    ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ItemCompatibilityDataLoader());
    ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DropsDataLoader());
  }
}
