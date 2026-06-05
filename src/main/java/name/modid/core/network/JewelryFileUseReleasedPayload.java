package name.modid.core.network;

import name.modid.Gemstones;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record JewelryFileUseReleasedPayload() implements CustomPayload {
  public static final Id<JewelryFileUseReleasedPayload> ID =
      new Id<>(Identifier.of(Gemstones.MOD_ID, "jewelry_file_use_released"));
  public static final PacketCodec<PacketByteBuf, JewelryFileUseReleasedPayload> CODEC =
      PacketCodec.unit(new JewelryFileUseReleasedPayload());

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}
