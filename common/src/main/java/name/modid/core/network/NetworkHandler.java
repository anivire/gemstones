package name.modid.core.network;

import name.modid.core.api.modifiers.helpers.GemstoneAttributeRefreshHelper;
import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.content.items.tools.JewelryFileItem;
import name.modid.core.utils.airJump.AirJumpLogic;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public final class NetworkHandler {
  public static void initialize() {
    NetworkManager.registerReceiver(NetworkManager.c2s(), AirJumpPayload.ID, AirJumpPayload.CODEC,
        (payload, context) -> context.queue(() -> {
          if (context.getPlayer() instanceof ServerPlayerEntity player
              && AirJumpLogic.canAirJumpServer(player)) {
            AirJumpLogic.applyAirJumpImpulse(player);
            AirJumpLogic.playEffect(player);
            AirJumpLogic.onAirJumpPerformed(player);
          }
        }));

    NetworkManager.registerReceiver(NetworkManager.c2s(), JewelryFileUseReleasedPayload.ID,
        JewelryFileUseReleasedPayload.CODEC, (payload, context) -> context.queue(() -> {
          if (!(context.getPlayer() instanceof ServerPlayerEntity player)) {
            return;
          }

          var fileStack = player.getStackInHand(Hand.MAIN_HAND);
          if (fileStack.getItem() instanceof JewelryFileItem) {
            fileStack.remove(ComponentsRegistry.polishingUseLock());
          }
        }));

    if (Platform.getEnvironment() == Env.SERVER) {
      NetworkManager.registerS2CPayloadType(OreVisionPayload.ID, OreVisionPayload.CODEC);
      NetworkManager.registerS2CPayloadType(DatapackSyncPayload.ID, DatapackSyncPayload.CODEC);
    }
  }

  public static void sendDatapackSync(ServerPlayerEntity player) {
    GemstoneAttributeRefreshHelper.refreshPlayer(player);

    if (NetworkManager.canPlayerReceive(player, DatapackSyncPayload.ID)) {
      NetworkManager.sendToPlayer(player, DatapackSyncPayload.current());
    }
  }
}
