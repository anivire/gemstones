package name.modid.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
  private static final float BASE_PULL_TIME = 20.0f;

  @Inject(method = "getFovMultiplier", at = @At("RETURN"), cancellable = true)
  private void modifyFovMultiplier(CallbackInfoReturnable<Float> cir) {
    AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) (Object) this;
    ItemStack stack = player.getActiveItem();

    if (stack.getItem() instanceof BowItem && player.isUsingItem()) {
      AttributeModifiersComponent itemAttributeModifiers = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,
          AttributeModifiersComponent.DEFAULT);

      float drawSpeedPercent = 0.0f;
      for (AttributeModifiersComponent.Entry mod : itemAttributeModifiers.modifiers()) {
        if (AttributesRegistry.PULL_SPEED_ATTRIBUTE == mod.attribute()) {
          drawSpeedPercent += (float) mod.modifier().value();
        }
      }

      float useTicks = stack.getMaxUseTime(player) - player.getItemUseTimeLeft();
      float adjustedTicks = useTicks * (1.0f + drawSpeedPercent);
      float progress = adjustedTicks / BASE_PULL_TIME;
      progress = (progress * progress + progress * 2.0f) / 3.0f;
      if (progress > 1.0f) {
        progress = 1.0f;
      }

      float fovMultiplier = cir.getReturnValue();
      fovMultiplier = 1.0f - progress * 0.15f;
      cir.setReturnValue(fovMultiplier);
    }
  }
}