package name.modid.core.network;

import java.util.LinkedHashMap;
import java.util.Map;

import name.modid.Gemstones;
import name.modid.datapack.drops.DropsDataLoader;
import name.modid.datapack.geodes.GeodesDataLoader;
import name.modid.datapack.items.ItemCompatibilityDataLoader;
import name.modid.datapack.modifiers.ModifiersDataLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DatapackSyncPayload(
    Map<String, String> gemstoneConfigs,
    Map<String, String> geodeConfigs,
    Map<String, String> itemCompatibilityConfigs,
    Map<String, String> dropsConfigs) implements CustomPayload {
  private static final int MAX_CONFIG_LENGTH = 1_048_576;

  public static final Id<DatapackSyncPayload> ID = new Id<>(
      Identifier.of(Gemstones.MOD_ID, "datapack_sync"));

  public static final PacketCodec<PacketByteBuf, DatapackSyncPayload> CODEC = PacketCodec.of((value, buf) -> {
    writeStringMap(buf, value.gemstoneConfigs);
    writeStringMap(buf, value.geodeConfigs);
    writeStringMap(buf, value.itemCompatibilityConfigs);
    writeStringMap(buf, value.dropsConfigs);
  }, buf -> new DatapackSyncPayload(readStringMap(buf), readStringMap(buf), readStringMap(buf), readStringMap(buf)));

  public static DatapackSyncPayload current() {
    return new DatapackSyncPayload(
        ModifiersDataLoader.getLoadedConfigSources(),
        GeodesDataLoader.getLoadedConfigSources(),
        ItemCompatibilityDataLoader.getLoadedConfigSources(),
        DropsDataLoader.getLoadedConfigSources());
  }

  private static void writeStringMap(PacketByteBuf buf, Map<String, String> values) {
    buf.writeVarInt(values.size());
    values.forEach((key, value) -> {
      buf.writeString(key);
      buf.writeString(value, MAX_CONFIG_LENGTH);
    });
  }

  private static Map<String, String> readStringMap(PacketByteBuf buf) {
    int size = buf.readVarInt();
    Map<String, String> values = new LinkedHashMap<>(size);

    for (int i = 0; i < size; i++) {
      values.put(buf.readString(), buf.readString(MAX_CONFIG_LENGTH));
    }

    return values;
  }

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}
