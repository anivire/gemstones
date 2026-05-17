package name.modid.core.network;

import java.util.List;

import name.modid.Gemstones;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record OreVisionPayload(List<OreVisionPayload.HighlightedOre> ores) implements CustomPayload {
  public static final Id<OreVisionPayload> ID = new Id<>(Identifier.of(Gemstones.MOD_ID, "ore_vision_update"));

  public record HighlightedOre(BlockPos pos, int color) {
  }

  public static final PacketCodec<PacketByteBuf, OreVisionPayload> CODEC = PacketCodec.of((value, buf) -> {
    buf.writeVarInt(value.ores.size());
    for (var ore : value.ores) {
      buf.writeBlockPos(ore.pos());
      buf.writeInt(ore.color());
    }
  }, buf -> {
    int size = buf.readVarInt();
    java.util.ArrayList<HighlightedOre> ores = new java.util.ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      ores.add(new HighlightedOre(buf.readBlockPos(), buf.readInt()));
    }
    return new OreVisionPayload(ores);
  });

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}
