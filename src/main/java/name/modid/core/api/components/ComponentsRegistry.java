package name.modid.core.api.components;

import com.mojang.serialization.Codec;

import name.modid.Gemstones;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ComponentsRegistry {
  private static final PacketCodec<RegistryByteBuf, Boolean> BOOLEAN_PACKET_CODEC = PacketCodec
      .of((value, buf) -> buf.writeBoolean(value), RegistryByteBuf::readBoolean);

  public static final ComponentType<GemstoneSlotsComponent> GEMSTONES = Registry.register(
      Registries.DATA_COMPONENT_TYPE, Identifier.of(Gemstones.MOD_ID, "gemstones"),
      ComponentType.<GemstoneSlotsComponent>builder().codec(GemstoneSlotsComponent.GEMSTONE_SLOTS_CODEC).build());

  public static final ComponentType<PolishingComponent> POLISHING = Registry.register(
      Registries.DATA_COMPONENT_TYPE, Identifier.of(Gemstones.MOD_ID, "polishing"),
      ComponentType.<PolishingComponent>builder().codec(PolishingComponent.CODEC)
          .packetCodec(PolishingComponent.PACKET_CODEC).build());

  // used to lock use key while polishing, this needed to reset polishing after
  // gemstone quality changes
  public static final ComponentType<Boolean> POLISHING_USE_LOCK = Registry.register(
      Registries.DATA_COMPONENT_TYPE, Identifier.of(Gemstones.MOD_ID, "polishing_use_lock"),
      ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(BOOLEAN_PACKET_CODEC).build());

  public static final ComponentType<Boolean> EXPLOSION_IMMUNE = Registry.register(
      Registries.DATA_COMPONENT_TYPE, Identifier.of(Gemstones.MOD_ID, "explosion_immune"),
      ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(BOOLEAN_PACKET_CODEC).build());

  // used to store original potion type for proper potion strength/time upgrade
  public static final ComponentType<Identifier> ORIGINAL_POTION = Registry.register(
      Registries.DATA_COMPONENT_TYPE, Identifier.of(Gemstones.MOD_ID, "original_potion"),
      ComponentType.<Identifier>builder().codec(Identifier.CODEC).build());

  public static void initialize() {
    Gemstones.LOGGER.info("Registering {} components", Gemstones.MOD_ID);
  }
}
