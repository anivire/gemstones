package name.modid.helpers.components;

import name.modid.Gemstones;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ComponentsHelper {
  // TODO: create gemstone type and quality for GemstoneItem
  public static final ComponentType<GemstoneSlots> GEMSTONES = Registry.register(
      Registries.DATA_COMPONENT_TYPE, Identifier.of(Gemstones.MOD_ID, "gemstones"),
      ComponentType.<GemstoneSlots>builder().codec(GemstoneSlots.GEMSTONE_SLOTS_CODEC).build());

  public static void initialize() {
    Gemstones.LOGGER.info("Registering {} components", Gemstones.MOD_ID);
  }
}
