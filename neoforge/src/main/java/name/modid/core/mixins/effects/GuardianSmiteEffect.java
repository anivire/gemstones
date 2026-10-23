package name.modid.core.mixins.effects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.utils.guardianSmite.GuardianSmiteHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@Mixin(LivingEntity.class)
public class GuardianSmiteEffect {
  // Guardian Smite adds its bonus to the next direct player hits instead of a separate magic hit,
  // so it does not reset i-frames and damage over time does not trigger it
  @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
  private float addSmiteBonus(float amount, DamageSource source) {
    return GuardianSmiteHelper.applyBonus((LivingEntity) (Object) this, source, amount);
  }

  @Inject(method = "damage", at = @At("RETURN"))
  private void spendSmiteCharge(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    GuardianSmiteHelper.finishHit((LivingEntity) (Object) this, cir.getReturnValue());
  }
}
