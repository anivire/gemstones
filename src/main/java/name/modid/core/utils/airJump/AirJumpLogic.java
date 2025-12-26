package name.modid.core.utils.airJump;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class AirJumpLogic {
  public static final int AIR_JUMP_COOLDOWN_TICKS = 4;
  public static final float AIR_JUMP_FALL_FORGIVENESS_PER_JUMP = 5.0f;
  public static final float AIR_JUMP_FALL_FORGIVENESS_MAX = 12.0f;
  public static final float AIR_JUMP_FALL_FORGIVENESS_DRAIN_PER_TICK = 0.25f;

  private AirJumpLogic() {
  }

  public static int getMaxAirJumps(PlayerEntity self) {
    double sum = ModifierUtils.collectAttributeValuesFromArmor(
        self, AttributesRegistry.JUMP_COUNT_ATTRIBUTE);
    return Math.max(0, (int) Math.floor(Math.max(0.0, sum)));
  }

  public static boolean canAirJumpClient(PlayerEntity player) {
    if (player.isOnGround())
      return false;
    if (player.isFallFlying())
      return false;
    if (player.isClimbing())
      return false;
    if (player.isTouchingWater() || player.isInLava())
      return false;

    AirJumpState state = AirJumpState.get(player);
    int max = getMaxAirJumps(player);
    if (max <= 0)
      return false;
    if (state.airJumpsUsed >= max)
      return false;
    if (state.cooldownTicks > 0)
      return false;
    if (state.groundGraceTicks > 0)
      return false;

    return true;
  }

  public static boolean canAirJumpServer(ServerPlayerEntity player) {
    if (player.isOnGround())
      return false;
    if (player.isFallFlying())
      return false;
    if (player.isClimbing())
      return false;
    if (player.isTouchingWater() || player.isInLava())
      return false;

    AirJumpState state = AirJumpState.get(player);
    int max = getMaxAirJumps(player);
    if (max <= 0)
      return false;
    if (state.airJumpsUsed >= max)
      return false;
    if (state.cooldownTicks > 0)
      return false;
    if (state.groundGraceTicks > 0)
      return false;

    return true;
  }

  public static void applyAirJumpImpulse(PlayerEntity player) {
    Vec3d vel = player.getVelocity();

    double horizontalSpeedSq = vel.x * vel.x + vel.z * vel.z;
    boolean isStanding = horizontalSpeedSq < 0.0001;

    double newY = Math.max(vel.y, 0.0) + 0.76;
    Vec3d look = player.getRotationVec(1.0f).normalize();

    double forwardBoost = isStanding ? 0.0 : 0.5;

    Vec3d boosted = new Vec3d(
        vel.x + look.x * forwardBoost,
        newY,
        vel.z + look.z * forwardBoost);

    player.setVelocity(boosted);
    player.velocityModified = true;
    player.fallDistance = 0.0f;
  }

  public static void onAirJumpPerformed(PlayerEntity player) {
    AirJumpState state = AirJumpState.get(player);
    state.airJumpsUsed += 1;
    state.cooldownTicks = AIR_JUMP_COOLDOWN_TICKS;

    state.fallForgivenessRemaining = Math.min(
        state.fallForgivenessRemaining + AIR_JUMP_FALL_FORGIVENESS_PER_JUMP,
        AIR_JUMP_FALL_FORGIVENESS_MAX);

    player.fallDistance = Math.max(0.0f, player.fallDistance - AIR_JUMP_FALL_FORGIVENESS_PER_JUMP * 0.5f);
    state.sync(player);
  }

  public static void onLanded(PlayerEntity player) {
    AirJumpState state = AirJumpState.get(player);
    state.airJumpsUsed = 0;
    state.cooldownTicks = 0;
    state.groundGraceTicks = 1;
    state.fallForgivenessRemaining = 0.0f;
    state.sync(player);
  }

  public static void tick(PlayerEntity player) {
    AirJumpState state = AirJumpState.get(player);
    if (state.cooldownTicks > 0)
      state.cooldownTicks--;
    if (state.groundGraceTicks > 0)
      state.groundGraceTicks--;

    if (!player.isOnGround() && state.fallForgivenessRemaining > 0.0f) {
      float drain = AIR_JUMP_FALL_FORGIVENESS_DRAIN_PER_TICK;
      float applied = Math.min(drain, state.fallForgivenessRemaining);
      state.fallForgivenessRemaining -= applied;
      player.fallDistance = Math.max(0.0f, player.fallDistance - applied);
    }
  }
}