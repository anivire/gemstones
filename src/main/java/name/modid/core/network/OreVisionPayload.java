package name.modid.core.network;

import java.util.List;

import name.modid.Gemstones;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record OreVisionPayload(List<BlockPos> ores) implements CustomPayload {
  public static final Id<OreVisionPayload> ID = new Id<>(Identifier.of(Gemstones.MOD_ID, "ore_vision_update"));

  public static final PacketCodec<PacketByteBuf, OreVisionPayload> CODEC = PacketCodec.of((value, buf) -> {
    buf.writeVarInt(value.ores.size());
    for (var pos : value.ores)
      buf.writeBlockPos(pos);
  }, buf -> {
    int size = buf.readVarInt();
    java.util.ArrayList<BlockPos> positions = new java.util.ArrayList<>(size);
    for (int i = 0; i < size; i++)
      positions.add(buf.readBlockPos());
    return new OreVisionPayload(positions);
  });

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}