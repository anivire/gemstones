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

    if (target.getHealth() == target.getMaxHealth()) {
      return CustomEvents.ON_FIRST_HIT.invoker().onFirstHit(target, source, amount);
    }

    return amount;
  }
}