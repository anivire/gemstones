package name.modid.core.mixins.modifiers;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

@Mixin(LivingEntity.class)
public class OnFirstHit {
  @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"), cancellable = true)
  private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    LivingEntity entity = (LivingEntity) (Object) this;
    ArrayList<ModifierOnFirstHitMelee> allModifiersOnFirstHit = new ArrayList<>();

    if (entity.getHealth() == entity.getMaxHealth()) {
      ItemStack itemStack = source.getWeaponStack();
      allModifiersOnFirstHit.addAll(ModifierGatheringHelper.getOnHitFirstModifiers(itemStack));

      if (!allModifiersOnFirstHit.isEmpty()) {
        double additionalDamagePercent = 0.0;
        for (ModifierOnFirstHitMelee modifier : allModifiersOnFirstHit) {
          if (modifier.getEventType() == EventType.ADDITIONAL_DAMAGE) {
            additionalDamagePercent += modifier.getValues().get(modifier.getRarityType());
          }
        }

        amount += amount * additionalDamagePercent;
      }
    }
  }

  @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
  private float modifyFirstHitDamage(float amount, DamageSource source) {
    LivingEntity target = (LivingEntity) (Object) this;

    if (target.getHealth() == target.getMaxHealth()) {
      Entity attacker = source.getAttacker();
      if (attacker instanceof LivingEntity livingAttacker) {
        ItemStack weapon = livingAttacker.getMainHandStack();

        if (weapon != null && !weapon.isEmpty() && GemstoneSlotHelper.isItemValid(weapon.getItem())
            && GemstoneSlotHelper.isGemstonesExists(weapon)) {
          ArrayList<ModifierOnFirstHitMelee> allModifiersOnFirstHit = ModifierGatheringHelper
              .getOnHitFirstModifiers(weapon);

          if (!allModifiersOnFirstHit.isEmpty()) {
            double additionalDamagePercent = 0.0;
            for (ModifierOnFirstHitMelee modifier : allModifiersOnFirstHit) {
              if (modifier.getEventType() == EventType.ADDITIONAL_DAMAGE) {
                additionalDamagePercent += modifier.getValues().get(modifier.getRarityType());
              }
            }
            amount += amount * additionalDamagePercent;
          }
        }
      }
    }

    return amount;
  }
}