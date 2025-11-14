package name.modid.core.mixins.effects.witherGuard;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.utils.WitherSkullOrbitFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(Entity.class)
public abstract class WitherSkullEntityTeleport {
  @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDLjava/util/Set;FF)Z", at = @At("HEAD"), cancellable = true)
  private void gemstones$preventOrbitSkullTeleport(ServerWorld world,
      double destX, double destY, double destZ,
      Set<?> flags, float yaw, float pitch,
      CallbackInfoReturnable<Boolean> cir) {

    Entity self = (Entity) (Object) this;

    if (self instanceof WitherSkullEntity skull &&
        ((WitherSkullOrbitFlag) (Object) skull).gemstones$isOrbiting()) {

      skull.discard();
      cir.setReturnValue(false);
      cir.cancel();
    }
  }
}