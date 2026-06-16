package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.content.registries.AttributesRegistry;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@Mixin(BowItem.class)
public class BowPullSpeedAttribute {
  private static float drawSpeedMultiplier = 1.0f;

  @Inject(method = "use", at = @At("HEAD"))
  private void onUse(World world, PlayerEntity user, Hand hand,
      CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
    ItemStack stack = user.getStackInHand(hand);
    AttributeModifiersComponent comp = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT);
    drawSpeedMultiplier = ModifierUtils.getAttributeMultiplier(
        comp, AttributesRegistry.PULL_SPEED_ATTRIBUTE);
  }

  @ModifyConstant(method = "getPullProgress", constant = @Constant(floatValue = 20.0F))
  private static float modifyPullTime(float original) {
    return original / drawSpeedMultiplier;
  }
}
