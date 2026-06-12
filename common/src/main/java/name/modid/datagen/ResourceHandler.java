package name.modid.datagen;

import name.modid.datapack.drops.DropsDataLoader;
import name.modid.datapack.geodes.GeodesDataLoader;
import name.modid.datapack.items.ItemCompatibilityDataLoader;
import name.modid.datapack.modifiers.ModifiersDataLoader;
import name.modid.datapack.sockets.SocketSettingsDataLoader;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resource.ResourceType;

public class ResourceHandler {
  public static void initialize() {
    ReloadListenerRegistry.register(ResourceType.SERVER_DATA, new ModifiersDataLoader(), ModifiersDataLoader.ID);
    ReloadListenerRegistry.register(ResourceType.SERVER_DATA, new GeodesDataLoader(), GeodesDataLoader.ID);
    ReloadListenerRegistry.register(ResourceType.SERVER_DATA, new ItemCompatibilityDataLoader(),
        ItemCompatibilityDataLoader.ID);
    ReloadListenerRegistry.register(ResourceType.SERVER_DATA, new DropsDataLoader(), DropsDataLoader.ID);
    ReloadListenerRegistry.register(ResourceType.SERVER_DATA, new SocketSettingsDataLoader(),
        SocketSettingsDataLoader.ID);
  }
}
