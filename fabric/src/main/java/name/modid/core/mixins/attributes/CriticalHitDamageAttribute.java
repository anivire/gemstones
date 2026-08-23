package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.api.modifiers.helpers.GemstoneAttributeModifiersHelper;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class CriticalHitDamageAttribute {
  @ModifyConstant(method = "attack", constant = @Constant(floatValue = 1.5F))
  private float modifyCriticalDamage(float f, Entity target) {
    PlayerEntity player = (PlayerEntity) (Object) this;
    AttributeModifiersComponent itemAttributeModifiers = GemstoneAttributeModifiersHelper
        .getEffectiveModifiers(player.getMainHandStack());

    return (float) ModifierUtils.getAttributeValue(
        itemAttributeModifiers,
        AttributesRegistry.CRIT_DAMAGE_ATTRIBUTE,
        f);
  }
}
