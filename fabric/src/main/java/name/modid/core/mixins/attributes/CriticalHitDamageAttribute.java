package name.modid.core.mixins.attributes;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class CriticalHitDamageAttribute {
  @ModifyExpressionValue(method = "attack", at = @At(value = "CONSTANT", args = "floatValue=1.5F"))
  private float modifyCriticalDamage(float f) {
    PlayerEntity player = (PlayerEntity) (Object) this;
    AttributeModifiersComponent itemAttributeModifiers = player.getMainHandStack()
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

    return (float) ModifierUtils.getAttributeValue(
        itemAttributeModifiers,
        AttributesRegistry.CRIT_DAMAGE_ATTRIBUTE,
        f);
  }
}
