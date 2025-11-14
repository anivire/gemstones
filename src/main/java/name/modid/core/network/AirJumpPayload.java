package name.modid.core.network;

import name.modid.Gemstones;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AirJumpPayload() implements CustomPayload {
  public static final Id<AirJumpPayload> ID = new Id<>(Identifier.of(Gemstones.MOD_ID, "air_jump"));
  public static final PacketCodec<PacketByteBuf, AirJumpPayload> CODEC = PacketCodec.unit(new AirJumpPayload());

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }

  public static void registerCodecs() {
    PayloadTypeRegistry.playC2S().register(ID, CODEC);
  }
}