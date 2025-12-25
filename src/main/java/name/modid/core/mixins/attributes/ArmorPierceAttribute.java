package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

// TODO: cleanup and refactor
@Mixin(PlayerEntity.class)
public abstract class ArmorPierceAttribute {

  private static final Identifier TEMP_APPLY_ARMOR_PIERCE_ID = Identifier.of("modid", "temp_apply_armor_pierce");

  private LivingEntity ap$currentTarget;
  private boolean ap$applied = false;

  @Shadow
  public abstract void attack(Entity target);

  @Inject(method = "attack", at = @At("HEAD"))
  private void ap$captureTarget(Entity target, CallbackInfo ci) {
    this.ap$currentTarget = target instanceof LivingEntity le ? le : null;
    this.ap$applied = false;
  }

  @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.BEFORE))
  private void ap$applyPierceBeforeDamage(Entity target, CallbackInfo ci) {
    if (!(this.ap$currentTarget instanceof LivingEntity livingTarget))
      return;
    PlayerEntity player = (PlayerEntity) (Object) this;

    ItemStack stack = player.getMainHandStack();
    if (stack.isEmpty())
      return;

    AttributeModifiersComponent comp = stack.getOrDefault(
        DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
    if (comp.modifiers().isEmpty())
      return;

    String pierceAttrIdStr = AttributesRegistry.ARMOR_PIERCE_ATTRIBUTE.getIdAsString();
    if (pierceAttrIdStr == null)
      return;

    double armorPierce = comp.modifiers().stream()
        .filter(m -> {
          String idStr = m.attribute().getIdAsString();
          return idStr != null && idStr.equals(pierceAttrIdStr);
        })
        .mapToDouble(m -> {
          EntityAttributeModifier mod = m.modifier();
          return mod.operation() == EntityAttributeModifier.Operation.ADD_VALUE
              ? mod.value()
              : 0.0;
        })
        .sum();

    if (armorPierce <= 0.0)
      return;

    EntityAttributeInstance armorAttr = livingTarget.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
    if (armorAttr == null)
      return;

    EntityAttributeModifier existing = armorAttr.getModifier(TEMP_APPLY_ARMOR_PIERCE_ID);
    if (existing != null) {
      armorAttr.removeModifier(existing);
    }

    EntityAttributeModifier minusArmor = new EntityAttributeModifier(
        TEMP_APPLY_ARMOR_PIERCE_ID,
        -armorPierce,
        EntityAttributeModifier.Operation.ADD_VALUE);

    armorAttr.addPersistentModifier(minusArmor);
    this.ap$applied = true;
  }

  @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", shift = At.Shift.AFTER))
  private void ap$removePierceAfterDamage(Entity target, CallbackInfo ci) {
    if (!this.ap$applied)
      return;
    if (!(this.ap$currentTarget instanceof LivingEntity livingTarget))
      return;

    EntityAttributeInstance armorAttr = livingTarget.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
    if (armorAttr != null) {
      EntityAttributeModifier curr = armorAttr.getModifier(TEMP_APPLY_ARMOR_PIERCE_ID);
      if (curr != null) {
        armorAttr.removeModifier(curr);
      }
    }
    this.ap$applied = false;
  }

  @Inject(method = "attack", at = @At("TAIL"))
  private void ap$clear(Entity target, CallbackInfo ci) {
    if (this.ap$applied && this.ap$currentTarget instanceof LivingEntity livingTarget) {
      EntityAttributeInstance armorAttr = livingTarget.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
      if (armorAttr != null) {
        EntityAttributeModifier curr = armorAttr.getModifier(TEMP_APPLY_ARMOR_PIERCE_ID);
        if (curr != null) {
          armorAttr.removeModifier(curr);
        }
      }
      this.ap$applied = false;
    }
    this.ap$currentTarget = null;
  }
}