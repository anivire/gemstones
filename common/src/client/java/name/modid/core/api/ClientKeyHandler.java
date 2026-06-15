package name.modid.core.api;

import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.content.items.tools.JewelryFileItem;
import name.modid.core.network.AirJumpPayload;
import name.modid.core.network.JewelryFileUseReleasedPayload;
import name.modid.core.utils.airJump.AirJumpLogic;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;

public final class ClientKeyHandler {
  private static boolean wasJumpHeld = false;
  private static boolean wasUseHeld = false;
  private static boolean wasOnGround = true;

  public static void initialize() {
    ClientTickEvent.CLIENT_POST.register(ClientKeyHandler::onEndClientTick);
  }

  private static void onEndClientTick(MinecraftClient client) {
    if (client.player == null || client.world == null)
      return;

    ClientPlayerEntity player = client.player;

    boolean onGroundNow = player.isOnGround();
    boolean justLeftGround = wasOnGround && !onGroundNow;
    wasOnGround = onGroundNow;

    GameOptions opts = client.options;

    // check for jewelry file use release, since it doesn't require the player
    // to be on the ground
    handleJewelryFileUseRelease(player, opts);

    boolean jumpHeld = opts.jumpKey.isPressed();

    boolean justPressed = jumpHeld && !wasJumpHeld;
    wasJumpHeld = jumpHeld;

    if (!justPressed)
      return;

    if (justLeftGround)
      return;

    if (AirJumpLogic.canAirJumpClient(player)) {
      NetworkManager.sendToServer(new AirJumpPayload());
    }
  }

  private static void handleJewelryFileUseRelease(ClientPlayerEntity player, GameOptions opts) {
    boolean useHeld = opts.useKey.isPressed();
    boolean justReleased = wasUseHeld && !useHeld;
    wasUseHeld = useHeld;

    if (!justReleased)
      return;

    var fileStack = player.getMainHandStack();
    if (fileStack.getItem() instanceof JewelryFileItem
        && fileStack.contains(ComponentsRegistry.polishingUseLock())) {
      fileStack.remove(ComponentsRegistry.polishingUseLock());
      NetworkManager.sendToServer(new JewelryFileUseReleasedPayload());
    }
  }
}
