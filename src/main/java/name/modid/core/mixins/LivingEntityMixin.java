package name.modid.core.mixins;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.api.attributes.AttributesRegistry;
import name.modid.core.api.effects.EffectsRegistry;
import name.modid.core.api.modifiers.EventType;
import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.impl.categories.ModifierOnFirstHitMelee;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

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
    StatusEffectInstance eff = target.getStatusEffect(EffectsRegistry.GUARDIAN_SMITE_EFFECT);
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

  @Inject(method = "tick", at = @At("RETURN"), cancellable = true)
  private void onTick(CallbackInfo ci) {
    if ((Object) this instanceof MobEntity mob
        && mob.hasStatusEffect(EffectsRegistry.STUNNED_EFFECT)) {
      mob.updateVelocity(0, mob.getVelocity());
    }
  }

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

    if (entity instanceof PlayerEntity player) {
      double totalEvasionChanceFromArmor = 0.0;

      for (EquipmentSlot slot : EquipmentSlot.values()) {
        if (slot == EquipmentSlot.CHEST || slot == EquipmentSlot.FEET
            || slot == EquipmentSlot.HEAD || slot == EquipmentSlot.LEGS) {
          ItemStack itemStack = player.getEquippedStack(slot);
          AttributeModifiersComponent modifiersComponent = itemStack
              .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

          Entry modifierEntry = modifiersComponent.modifiers().stream()
              .filter(x -> x.attribute() == AttributesRegistry.EVASION_ATTRIBUTE).findFirst()
              .orElse(null);

          if (modifierEntry != null) {
            for (Entry e : modifiersComponent.modifiers()) {
              if (e.attribute() == AttributesRegistry.EVASION_ATTRIBUTE) {
                totalEvasionChanceFromArmor += e.modifier().value();
              }
            }
          }
        }
      }

      double finalEvasionChance = Math.min(1.0, totalEvasionChanceFromArmor);

      if (entity.getRandom().nextDouble() < finalEvasionChance) {
        cir.setReturnValue(false);
        ServerWorld serverWorld = (ServerWorld) entity.getWorld();

        serverWorld.playSound(
            null,
            entity.getX(),
            entity.getY(),
            entity.getZ(),
            SoundEvents.ENTITY_ENDERMAN_TELEPORT,
            SoundCategory.PLAYERS,
            1.0F,
            1.0F);

        serverWorld.spawnParticles(
            ParticleTypes.CLOUD,
            entity.getX(),
            entity.getY() + 1.0,
            entity.getZ(),
            20,
            0.3,
            0.3,
            0.3,
            0.2);
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
