package name.modid.core.utils.airJump;

import net.minecraft.entity.player.PlayerEntity;

public final class AirJumpState {
  public int airJumpsUsed = 0;
  public int cooldownTicks = 0;
  public int groundGraceTicks = 0;
  public float fallForgivenessRemaining = 0.0f;

  public static AirJumpState get(PlayerEntity player) {
    if (player instanceof AirJumpStateHolder holder) {
      return holder.getAirJumpState();
    }
    return new AirJumpState();
  }

  public void sync(PlayerEntity player) {
  }

  public interface AirJumpStateHolder {
    AirJumpState getAirJumpState();
  }
}