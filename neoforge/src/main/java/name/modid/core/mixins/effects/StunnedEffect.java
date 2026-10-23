package name.modid.core.mixins.effects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;

@Mixin(LivingEntity.class)
public class StunnedEffect {
  // The movement speed modifier does not stop flying mobs (they use their own move controls),
  // so stunned mobs lose their own motion every tick. Ground mobs keep falling.
  @Inject(method = "tick", at = @At("RETURN"))
  private void onTick(CallbackInfo ci) {
    if ((Object) this instanceof MobEntity mob
        && !mob.getWorld().isClient()
        && mob.hasStatusEffect(EffectsRegistry.stunnedEntry())) {
      Vec3d velocity = mob.getVelocity();
      mob.setVelocity(0.0, mob.hasNoGravity() ? 0.0 : Math.min(velocity.y, 0.0), 0.0);
      mob.velocityModified = true;
    }
  }
}
