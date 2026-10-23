package name.modid.core.mixins.modifiers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.content.events.CustomEvents;
import name.modid.core.utils.accessors.DamageCooldownAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@Mixin(LivingEntity.class)
public abstract class OnLivingDamaged implements DamageCooldownAccessor {
  @Shadow
  protected float lastDamageTaken;

  @Override
  public float gemstones$getLastDamageTaken() {
    return this.lastDamageTaken;
  }

  @Override
  public void gemstones$setLastDamageTaken(float value) {
    this.lastDamageTaken = value;
  }

  // Architectury LIVING_HURT fires before i-frames and shields are checked, this one only after real damage
  @Inject(method = "damage", at = @At("RETURN"))
  private void onDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    LivingEntity self = (LivingEntity) (Object) this;

    if (!cir.getReturnValue() || self.getWorld().isClient()) {
      return;
    }

    CustomEvents.ON_LIVING_DAMAGED.invoker().onLivingDamaged(self, source, amount);
  }
}
