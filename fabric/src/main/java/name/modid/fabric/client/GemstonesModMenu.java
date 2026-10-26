package name.modid.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import name.modid.compat.cloth.ClothConfigCompat;
import name.modid.compat.cloth.GemstonesConfigScreen;

public class GemstonesModMenu implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return parent -> ClothConfigCompat.isLoaded() ? GemstonesConfigScreen.create(parent) : null;
  }
}
