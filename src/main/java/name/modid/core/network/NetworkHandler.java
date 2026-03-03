package name.modid.core.network;

import name.modid.core.utils.airJump.AirJumpLogic;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public final class NetworkHandler {
  public static void initialize() {
    AirJumpPayload.registerCodecs();
    PayloadTypeRegistry.playS2C().register(OreVisionPayload.ID, OreVisionPayload.CODEC);

    ServerPlayNetworking.registerGlobalReceiver(
        AirJumpPayload.ID,
        (payload, context) -> {
          context.player().getServer().execute(() -> {
            ServerPlayerEntity player = context.player();

            if (player != null && AirJumpLogic.canAirJumpServer(player)) {
              AirJumpLogic.applyAirJumpImpulse(player);
              AirJumpLogic.playEffect(player);
              AirJumpLogic.onAirJumpPerformed(player);
            }
          });
        });
  }
}