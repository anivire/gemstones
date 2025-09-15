package name.modid.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
  /**
   * Replace default critical damage {@code 1.5F} with value based on
   * {@code CRIT_DAMAGE_ATTRIBUTE}.
   */
  @ModifyConstant(method = "attack", constant = @Constant(floatValue = 1.5F))
  private float modifyCriticalDamage(float f, Entity target) {
    PlayerEntity player = (PlayerEntity) (Object) this;
    float bonusCritDamagePercent = 1.5f;
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
