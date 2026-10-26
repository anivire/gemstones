package name.modid.core.mixins.attributes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import name.modid.Gemstones;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Mixin(PlayerEntity.class)
public abstract class ArmorPierceAttribute {

  private static final Identifier TEMP_APPLY_ARMOR_PIERCE_ID = Identifier.of(Gemstones.MOD_ID,
      "temporary_armor_pierce");

  @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
  private boolean applyArmorPierce(Entity target, DamageSource source, float damage, Operation<Boolean> original) {
    if (!(target instanceof LivingEntity livingTarget)) {
      return original.call(target, source, damage);
    }

    EntityAttributeInstance armorAttr = livingTarget.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
    boolean applied = armorAttr != null && applyPierce(armorAttr);

    try {
      return original.call(target, source, damage);
    } finally {
      if (applied) {
        armorAttr.removeModifier(TEMP_APPLY_ARMOR_PIERCE_ID);
      }
    }
  }

  private boolean applyPierce(EntityAttributeInstance armorAttr) {
    PlayerEntity player = (PlayerEntity) (Object) this;
    ItemStack stack = player.getMainHandStack();

    if (stack.isEmpty()) {
      return false;
    }

    AttributeModifiersComponent comp = stack.getOrDefault(
        DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT);

    if (comp.modifiers().isEmpty()) {
      return false;
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
      return false;
    }

    armorPierce = Math.min(armorPierce, currentArmor);

    armorAttr.removeModifier(TEMP_APPLY_ARMOR_PIERCE_ID);
    armorAttr.addTemporaryModifier(new EntityAttributeModifier(
        TEMP_APPLY_ARMOR_PIERCE_ID,
        -armorPierce,
        EntityAttributeModifier.Operation.ADD_VALUE));
    return true;
  }
}
