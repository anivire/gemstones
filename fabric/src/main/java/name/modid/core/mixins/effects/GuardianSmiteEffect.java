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
  private static final ThreadLocal<Boolean> IS_APPLYING_GUARDIAN_SMITE = ThreadLocal.withInitial(() -> false);

  // While affected Guardian Smite debuff recieving bonus damage on hit
  @Inject(method = "damage", at = @At("RETURN"))
  private void bonusMagic(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    if (IS_APPLYING_GUARDIAN_SMITE.get())
      return;

    LivingEntity target = (LivingEntity) (Object) this;
    StatusEffectInstance effect = target.getStatusEffect(EffectsRegistry.guardianSmiteEntry());

    if (effect != null
        && target.getWorld() != null
        && target.getWorld().getServer() != null) {
      float bonusDamage = 3.0F * (effect.getAmplifier() + 1);

      target.getWorld().getServer().execute(() -> {
        if (target.isAlive()) {
          IS_APPLYING_GUARDIAN_SMITE.set(true);
          try {
            target.hurtTime = 0;
            target.timeUntilRegen = 0;
            target.damage(target.getDamageSources().magic(), bonusDamage);
          } finally {
            IS_APPLYING_GUARDIAN_SMITE.set(false);
          }
        }
      });
    }
  }
}
