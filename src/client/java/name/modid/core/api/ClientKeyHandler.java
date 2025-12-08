package name.modid.core.api;

import name.modid.core.network.AirJumpPayload;
import name.modid.core.utils.airJump.AirJumpLogic;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;

public final class ClientKeyHandler {
  private static boolean wasJumpHeld = false;
  private static boolean wasOnGround = true;

  public static void initialize() {
    ClientTickEvents.END_CLIENT_TICK.register(ClientKeyHandler::onEndClientTick);
  }

  private static void onEndClientTick(MinecraftClient client) {
    if (client.player == null || client.world == null)
      return;

    ClientPlayerEntity player = client.player;

    boolean onGroundNow = player.isOnGround();
    boolean justLeftGround = wasOnGround && !onGroundNow;
    wasOnGround = onGroundNow;

    GameOptions opts = client.options;
    boolean jumpHeld = opts.jumpKey.isPressed();

    boolean justPressed = jumpHeld && !wasJumpHeld;
    wasJumpHeld = jumpHeld;

    if (!justPressed)
      return;

    if (justLeftGround)
      return;

    if (AirJumpLogic.canAirJumpClient(player)) {
      ClientPlayNetworking.send(new AirJumpPayload());
    }
  }
}