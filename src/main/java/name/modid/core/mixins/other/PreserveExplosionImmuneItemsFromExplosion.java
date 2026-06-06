package name.modid.core.mixins.other;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.api.components.ItemExplosionImmunity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;

@Mixin(ItemEntity.class)
public abstract class PreserveExplosionImmuneItemsFromExplosion {
  @Shadow
  public abstract ItemStack getStack();

  @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
  private void ignoreExplosionDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    if (source.isIn(DamageTypeTags.IS_EXPLOSION) && ItemExplosionImmunity.isExplosionImmuneStack(this.getStack())) {
      cir.setReturnValue(false);
    }
  }
}
