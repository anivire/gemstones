package name.modid.core.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;

@Mixin(HeldItemRenderer.class)
public class BowPullSpeedHeldItemRenderer {
  @ModifyExpressionValue(method = "renderFirstPersonItem", at = @At(value = "CONSTANT", args = "floatValue=20.0F"), require = 0)
  private float gemstones$adjustBowPullAnimationTime(float pullTime, @Local(argsOnly = true) ItemStack item) {
    if (!(item.getItem() instanceof BowItem)) {
      return pullTime;
    }

    AttributeModifiersComponent modifiers = item.getOrDefault(
        DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT);

    return pullTime / ModifierUtils.getAttributeMultiplier(
        modifiers,
        AttributesRegistry.PULL_SPEED_ATTRIBUTE);
  }
}
