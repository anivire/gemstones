package name.modid.core.utils.airJump;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class AirJumpLogic {
  public static final int AIR_JUMP_COOLDOWN_TICKS = 4;
  public static final float AIR_JUMP_FALL_FORGIVENESS_PER_JUMP = 5.0f;
  public static final float AIR_JUMP_FALL_FORGIVENESS_MAX = 16.0f;
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

    EntityAttributeInstance safeFall = player.getAttributeInstance(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE);
    if (safeFall != null) {
      safeFall.getModifiers().stream()
          .filter(m -> m.idMatches(Identifier.of(Gemstones.MOD_ID, "air_jump_safe_fall_bonus")))
          .findFirst()
          .ifPresent(safeFall::removeModifier);

      double appliedBonus = Math.min(
          AIR_JUMP_FALL_FORGIVENESS_PER_JUMP * state.airJumpsUsed,
          AIR_JUMP_FALL_FORGIVENESS_MAX);

      EntityAttributeModifier modifier = new EntityAttributeModifier(
          Identifier.of(Gemstones.MOD_ID, "air_jump_safe_fall_bonus"),
          appliedBonus,
          EntityAttributeModifier.Operation.ADD_VALUE);

      safeFall.addTemporaryModifier(modifier);
    }

    state.sync(player);
  }

  public static void onLanded(PlayerEntity player) {
    AirJumpState state = AirJumpState.get(player);
    state.airJumpsUsed = 0;
    state.cooldownTicks = 0;
    state.groundGraceTicks = 1;
    state.fallForgivenessRemaining = 0.0f;
    state.removeSafeFallDelay = 2;

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

    if (state.removeSafeFallDelay > 0) {
      state.removeSafeFallDelay--;
      if (state.removeSafeFallDelay == 0) {
        var safeFall = player.getAttributeInstance(
            EntityAttributes.GENERIC_SAFE_FALL_DISTANCE);
        if (safeFall != null) {
          safeFall.getModifiers().stream()
              .filter(m -> m.idMatches(Identifier.of(Gemstones.MOD_ID, "air_jump_safe_fall_bonus")))
              .findFirst()
              .ifPresent(safeFall::removeModifier);
        }
      }
    }
  }

  public static void playEffect(PlayerEntity player) {
    World world = player.getWorld();

    world.playSound(
        null,
        player.getX(),
        player.getY(),
        player.getZ(),
        SoundEvents.ENTITY_PHANTOM_FLAP,
        SoundCategory.PLAYERS,
        1.5f,
        1.1f);

    if (!(world instanceof ServerWorld serverWorld))
      return;

    serverWorld.spawnParticles(
        ParticleTypes.CLOUD,
        player.getX(), player.getY() + 0.95, player.getZ(),
        18,
        0.25, 0.15, 0.25,
        0.1);
  }
}