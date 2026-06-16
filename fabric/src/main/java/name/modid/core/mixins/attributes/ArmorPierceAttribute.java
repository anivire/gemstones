package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.Gemstones;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Mixin(PlayerEntity.class)
public abstract class ArmorPierceAttribute {

  private static final Identifier TEMP_APPLY_ARMOR_PIERCE_ID = Identifier.of(Gemstones.MOD_ID,
      "temporary_armor_pierce");

  private LivingEntity currentTarget;
  private boolean applied = false;

  @Shadow
  public abstract void attack(Entity target);

  @Inject(method = "attack", at = @At("HEAD"))
  private void captureTarget(Entity target, CallbackInfo ci) {
    this.currentTarget = target instanceof LivingEntity living ? living : null;
    this.applied = false;
  }

  @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.BEFORE))
  private void applyPierceBeforeDamage(Entity target, CallbackInfo ci) {
    if (!(this.currentTarget instanceof LivingEntity livingTarget)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) (Object) this;
    ItemStack stack = player.getMainHandStack();

    if (stack.isEmpty()) {
      return;
    }

    AttributeModifiersComponent comp = stack.getOrDefault(
        DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT);

    if (comp.modifiers().isEmpty()) {
      return;
    }

    EntityAttributeInstance armorAttr = livingTarget.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);

    if (armorAttr == null) {
      return;
    }

    double baseArmor = armorAttr.getBaseValue();
    double currentArmor = armorAttr.getValue();

    double armorPierce = comp.modifiers().stream()
        .filter(m -> m.attribute().equals(AttributesRegistry.ARMOR_PIERCE_ATTRIBUTE))
        .mapToDouble(
            m -> {
              EntityAttributeModifier mod = m.modifier();

              return switch (mod.operation()) {
                case ADD_VALUE -> mod.value();
                case ADD_MULTIPLIED_BASE -> baseArmor * mod.value();
                case ADD_MULTIPLIED_TOTAL -> currentArmor * mod.value();
              };
            })
        .sum();

    if (armorPierce <= 0.0) {
      return;
    }

    armorPierce = Math.min(armorPierce, currentArmor);

    EntityAttributeModifier existing = armorAttr.getModifier(TEMP_APPLY_ARMOR_PIERCE_ID);

    if (existing != null) {
      armorAttr.removeModifier(existing);
    }

    EntityAttributeModifier minusArmor = new EntityAttributeModifier(
        TEMP_APPLY_ARMOR_PIERCE_ID,
        -armorPierce,
        EntityAttributeModifier.Operation.ADD_VALUE);

    armorAttr.addTemporaryModifier(minusArmor);
    this.applied = true;
  }

  @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.AFTER))
  private void removePierceAfterDamage(Entity target, CallbackInfo ci) {
    this.cleanup();
  }

  @Inject(method = "attack", at = @At("TAIL"))
  private void clear(Entity target, CallbackInfo ci) {
    this.cleanup();
    this.currentTarget = null;
  }

  private void cleanup() {
    if (!this.applied || !(this.currentTarget instanceof LivingEntity living)) {
      return;
    }

    EntityAttributeInstance armorAttr = living.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);

    if (armorAttr != null) {
      EntityAttributeModifier curr = armorAttr.getModifier(TEMP_APPLY_ARMOR_PIERCE_ID);

      if (curr != null) {
        armorAttr.removeModifier(curr);
      }
    }

    this.applied = false;
  }
}
