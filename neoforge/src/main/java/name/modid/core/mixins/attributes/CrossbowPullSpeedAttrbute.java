package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.content.registries.AttributesRegistry;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;

@Mixin(CrossbowItem.class)
public class CrossbowPullSpeedAttrbute {
  @Inject(method = "getPullTime", at = @At("RETURN"), cancellable = true)
  private static void gemstones$modifyPullTime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
    AttributeModifiersComponent itemAttributeModifiers = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT);
    float drawSpeedMultiplier = ModifierUtils.getAttributeMultiplier(
        itemAttributeModifiers,
        AttributesRegistry.PULL_SPEED_ATTRIBUTE);

    int modifiedPullTime = Math.max(1, Math.round(cir.getReturnValue() / drawSpeedMultiplier));
    cir.setReturnValue(modifiedPullTime);
  }
}
