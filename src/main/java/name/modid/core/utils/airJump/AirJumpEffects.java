// src/main/java/name/modid/core/network/AirJumpEffects.java
package name.modid.core.utils.airJump;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public final class AirJumpEffects {
  private AirJumpEffects() {
  }

  public static void play(PlayerEntity player) {
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