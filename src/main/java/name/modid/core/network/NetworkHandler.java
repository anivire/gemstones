package name.modid.core.network;

import name.modid.core.api.modifiers.helpers.GemstoneAttributeRefreshHelper;
import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.content.items.tools.JewelryFileItem;
import name.modid.core.utils.airJump.AirJumpLogic;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public final class NetworkHandler {
  public static void initialize() {
    AirJumpPayload.registerCodecs();
    PayloadTypeRegistry.playC2S().register(JewelryFileUseReleasedPayload.ID, JewelryFileUseReleasedPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(OreVisionPayload.ID, OreVisionPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(DatapackSyncPayload.ID, DatapackSyncPayload.CODEC);

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

    ServerPlayNetworking.registerGlobalReceiver(
        JewelryFileUseReleasedPayload.ID,
        (payload, context) -> {
          context.player().getServer().execute(() -> {
            ServerPlayerEntity player = context.player();
            if (player == null)
              return;

            var fileStack = player.getStackInHand(Hand.MAIN_HAND);
            if (fileStack.getItem() instanceof JewelryFileItem)
              fileStack.remove(ComponentsRegistry.POLISHING_USE_LOCK);
          });
        });

    ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
      if (success) {
        GemstoneAttributeRefreshHelper.refreshOnlinePlayers(server);
      }
    });

    ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
      GemstoneAttributeRefreshHelper.refreshPlayer(player);

      if (ServerPlayNetworking.canSend(player, DatapackSyncPayload.ID)) {
        ServerPlayNetworking.send(player, DatapackSyncPayload.current());
      }
    });
  }
}
