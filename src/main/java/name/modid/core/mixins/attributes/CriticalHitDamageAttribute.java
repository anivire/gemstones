package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class CriticalHitDamageAttribute {
  @ModifyConstant(method = "attack", constant = @Constant(floatValue = 1.5F))
  private float modifyCriticalDamage(float f, Entity target) {
    PlayerEntity player = (PlayerEntity) (Object) this;
    float bonusCritDamagePercent = 1.5f; // 1.5F default value
    AttributeModifiersComponent itemAttributeModifiers = player.getMainHandStack()
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

    for (AttributeModifiersComponent.Entry mod : itemAttributeModifiers.modifiers()) {
      if (AttributesRegistry.CRIT_DAMAGE_ATTRIBUTE.equals(mod.attribute())) {
        bonusCritDamagePercent += (float) mod.modifier().value();
      }
    }

    return bonusCritDamagePercent;
  }
}
