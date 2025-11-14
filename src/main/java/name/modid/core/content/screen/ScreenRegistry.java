package name.modid.core.content.screen;

import name.modid.Gemstones;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ScreenRegistry {
  public static final ScreenHandlerType<JewelryTableScreenHandler> JEWELRY_TABLE_SCREEN_HANDLER = Registry.register(
      Registries.SCREEN_HANDLER,
      Identifier.of(Gemstones.MOD_ID, "jewelry_table"),
      new ExtendedScreenHandlerType<JewelryTableScreenHandler, BlockPos>(
          JewelryTableScreenHandler::new,
          BlockPos.PACKET_CODEC));

  public static void initialize() {
    Gemstones.LOGGER.info("Registering screens for ", Gemstones.MOD_ID);
  }
}
