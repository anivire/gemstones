package name.modid.core.content.screen;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import name.modid.Gemstones;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;

public class ScreenRegistry {
  public static final DeferredRegister<ScreenHandlerType<?>> SCREEN_HANDLERS = DeferredRegister.create(Gemstones.MOD_ID,
      RegistryKeys.SCREEN_HANDLER);

  public static final RegistrySupplier<ScreenHandlerType<JewelryTableScreenHandler>> JEWELRY_TABLE_SCREEN_HANDLER = SCREEN_HANDLERS
      .register("jewelry_table", () -> MenuRegistry.ofExtended(JewelryTableScreenHandler::new));

  public static void initialize() {
    Gemstones.LOGGER.info("Registering screens for {}", Gemstones.MOD_ID);
    SCREEN_HANDLERS.register();
  }
}