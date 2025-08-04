package name.modid.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import name.modid.entities.EffectRegistrationHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
  @Inject(method = "damage", at = @At("RETURN"))
  private void bonusMagic(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    if (!cir.getReturnValue()) {
      return;
    }

    if (source.isOf(DamageTypes.MAGIC)) {
      return;
    }

    LivingEntity target = (LivingEntity) (Object) this;
    StatusEffectInstance eff =
        target.getStatusEffect(EffectRegistrationHelper.GUARDIAN_SMITE_EFFECT);
    if (eff == null) {
      return;
    }

    float magicBonus = 3.0F * (eff.getAmplifier() + 1);

    target.getWorld().getServer().execute(() -> {
      if (!target.isAlive())
        return;
      target.hurtTime = 0;
      target.timeUntilRegen = 0;
      target.damage(target.getDamageSources().magic(), magicBonus);
    });
  }
}
