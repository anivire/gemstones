package name.modid.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.helpers.attributes.AttributeRegistrationHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
  private static final float BASE_PULL_TIME = 1.25F;

  @Inject(method = "getPullProgress", at = @At("RETURN"), cancellable = true)
  private static void getPullProgress(int useTicks, ItemStack stack, LivingEntity user,
      CallbackInfoReturnable<Float> cir) {
    if (user == null) {
      cir.setReturnValue(0.0f);
    }

    float drawSpeedPercent = 0.0f;
    AttributeModifiersComponent itemAttributeModifiers = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT);
    for (AttributeModifiersComponent.Entry mod : itemAttributeModifiers.modifiers()) {
      if (AttributeRegistrationHelper.PULL_SPEED_ATTRIBUTE.equals(mod.attribute())) {
        drawSpeedPercent += (float) mod.modifier().value();
      }
    }

    float baseChargeTime = EnchantmentHelper.getCrossbowChargeTime(stack, user, BASE_PULL_TIME);
    float modifiedChargeTime = baseChargeTime / (1.0f + drawSpeedPercent);
    float modifiedChargeTicks = modifiedChargeTime * 20.0f;
    float progress = (float) useTicks / modifiedChargeTicks;

    cir.setReturnValue(MathHelper.clamp(progress, 0.0f, 1.0f));
  }
}