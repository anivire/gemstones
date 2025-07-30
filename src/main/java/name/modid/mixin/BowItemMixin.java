package name.modid.mixin;

import name.modid.helpers.attributes.AttributeRegistrationHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BowItem.class)
public class BowItemMixin {
  private static final float BASE_PULL_TIME = 20.0f;
  private static float drawSpeedPercent = 0.0f;

  @Inject(method = "use", at = @At("HEAD"))
  private void onUse(World world, PlayerEntity user, Hand hand,
      CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
    ItemStack stack = user.getStackInHand(hand);
    AttributeModifiersComponent itemAttributeModifiers = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT);

    drawSpeedPercent = 0.0f;
    for (AttributeModifiersComponent.Entry mod : itemAttributeModifiers.modifiers()) {
      if (AttributeRegistrationHelper.PULL_SPEED_ATTRIBUTE == mod.attribute()) {
        drawSpeedPercent += (float) mod.modifier().value();
      }
    }
  }

  @Inject(method = "getPullProgress", at = @At("RETURN"), cancellable = true)
  private static void getPullProgress(int useTicks, CallbackInfoReturnable<Float> cir) {
    float adjustedTicks = useTicks * (1.0f + drawSpeedPercent);
    float progress = adjustedTicks / BASE_PULL_TIME;
    progress = (progress * progress + progress * 2.0f) / 3.0f;
    if (progress > 1.0f) {
      progress = 1.0f;
    }
    cir.setReturnValue(progress);
  }
}