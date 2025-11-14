package name.modid.core.network;

import name.modid.core.utils.airJump.AirJumpEffects;
import name.modid.core.utils.airJump.AirJumpLogic;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public final class NetworkHandler {

  public static void registerClient() {
  }

  public static void registerServer() {
    ServerPlayNetworking.registerGlobalReceiver(
        AirJumpPayload.ID,
        (payload, context) -> {
          context.player().getServer().execute(() -> {
            ServerPlayerEntity player = context.player();
            if (player == null)
              return;

            if (AirJumpLogic.canAirJumpServer(player)) {
              AirJumpLogic.applyAirJumpImpulse(player);
              AirJumpEffects.play(player);
              AirJumpLogic.onAirJumpPerformed(player);
            }
          });
        });
  }
}