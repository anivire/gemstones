package name.modid.core.mixins.effects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(LivingEntity.class)
public class GuardianSmiteEffect {
  // While affected Guardian Smite debuff recieving bonus damage on hit
  @Inject(method = "damage", at = @At("RETURN"))
  private void bonusMagic(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    LivingEntity target = (LivingEntity) (Object) this;
    StatusEffectInstance effect = target.getStatusEffect(EffectsRegistry.GUARDIAN_SMITE_EFFECT);

    if (effect != null) {
      float bonusDamage = 3.0F * (effect.getAmplifier() + 1);

      target.getWorld().getServer().execute(() -> {
        if (target.isAlive()) {
          target.hurtTime = 0;
          target.timeUntilRegen = 0;
          target.damage(target.getDamageSources().magic(), bonusDamage);
        }
      });
    }
  }
}
