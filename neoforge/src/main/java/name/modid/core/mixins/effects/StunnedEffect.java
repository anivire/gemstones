package name.modid.core.mixins.effects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

@Mixin(LivingEntity.class)
public class StunnedEffect {
  @Inject(method = "tick", at = @At("RETURN"), cancellable = true)
  private void onTick(CallbackInfo ci) {
    if ((Object) this instanceof MobEntity mob
        && mob.hasStatusEffect(EffectsRegistry.stunnedEntry())) {
      mob.updateVelocity(0, mob.getVelocity());
    }
  }
}
