package name.modid.core.mixins.modifiers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import name.modid.core.content.events.CustomEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@Mixin(LivingEntity.class)
public class OnFirstHit {
  @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0)
  private float modifyOnFirstHit(float amount, DamageSource source) {
    LivingEntity target = (LivingEntity) (Object) this;

    float result = amount;

    if (target.getHealth() == target.getMaxHealth()) {
      result = CustomEvents.ON_FIRST_HIT.invoker().onFirstHit(target, source, result);
    }

    // Bonus damage (Garnet armor) is added into the hit, so it never needs a separate hit with an i-frame reset
    return CustomEvents.ON_OUTGOING_DAMAGE.invoker().onOutgoingDamage(target, source, result);
  }
}