package name.modid.core.mixins.other;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import name.modid.core.utils.amber.AmberMagnetHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

// Experience from blocks broken with an Amber tool appears right at the player
@Mixin(ExperienceOrbEntity.class)
public class AmberMagnetExperience {
  @ModifyVariable(method = "spawn", at = @At("HEAD"), argsOnly = true)
  private static Vec3d moveExperienceToPlayer(Vec3d pos, ServerWorld world) {
    return AmberMagnetHelper.getExperiencePosition(world, pos);
  }
}
