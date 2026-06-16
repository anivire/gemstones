package name.modid.core.api.components;

import com.mojang.serialization.Codec;

import name.modid.Gemstones;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ComponentsRegistry {
  public static volatile boolean INITIALIZED = false;

  private static final DeferredRegister<ComponentType<?>> COMPONENTS = DeferredRegister.create(
      Gemstones.MOD_ID,
      RegistryKeys.DATA_COMPONENT_TYPE);

  private static final PacketCodec<RegistryByteBuf, Boolean> BOOLEAN_PACKET_CODEC = PacketCodec
      .of((value, buf) -> buf.writeBoolean(value), RegistryByteBuf::readBoolean);

  private static final RegistrySupplier<ComponentType<?>> GEMSTONES_SUPPLIER = COMPONENTS.register(
      "gemstones",
      () -> ComponentType.<GemstoneSlotsComponent>builder().codec(GemstoneSlotsComponent.GEMSTONE_SLOTS_CODEC).build());

  private static final RegistrySupplier<ComponentType<?>> POLISHING_SUPPLIER = COMPONENTS.register(
      "polishing",
      () -> ComponentType.<PolishingComponent>builder().codec(PolishingComponent.CODEC)
          .packetCodec(PolishingComponent.PACKET_CODEC).build());

  // used to lock use key while polishing, this needed to reset polishing after
  // gemstone quality changes
  private static final RegistrySupplier<ComponentType<?>> POLISHING_USE_LOCK_SUPPLIER = COMPONENTS.register(
      "polishing_use_lock",
      () -> ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(BOOLEAN_PACKET_CODEC).build());

  private static final RegistrySupplier<ComponentType<?>> EXPLOSION_IMMUNE_SUPPLIER = COMPONENTS.register(
      "explosion_immune",
      () -> ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(BOOLEAN_PACKET_CODEC).build());

  // used to store original potion type for proper potion strength/time upgrade
  private static final RegistrySupplier<ComponentType<?>> ORIGINAL_POTION_SUPPLIER = COMPONENTS.register(
      "original_potion",
      () -> ComponentType.<Identifier>builder().codec(Identifier.CODEC).build());

  public static ComponentType<GemstoneSlotsComponent> gemstones() {
    return get(GEMSTONES_SUPPLIER);
  }

  public static ComponentType<PolishingComponent> polishing() {
    return get(POLISHING_SUPPLIER);
  }

  public static ComponentType<Boolean> polishingUseLock() {
    return get(POLISHING_USE_LOCK_SUPPLIER);
  }

  public static ComponentType<Boolean> explosionImmune() {
    return get(EXPLOSION_IMMUNE_SUPPLIER);
  }

  public static ComponentType<Identifier> originalPotion() {
    return get(ORIGINAL_POTION_SUPPLIER);
  }

  public static void initialize() {
    COMPONENTS.register();
    Gemstones.LOGGER.info("Registering {} components", Gemstones.MOD_ID);
    INITIALIZED = true;
  }

  @SuppressWarnings("unchecked")
  private static <T> ComponentType<T> get(RegistrySupplier<ComponentType<?>> supplier) {
    try {
      return (ComponentType<T>) supplier.get();
    } catch (NullPointerException e) {
      return null;
    }
  }
}
