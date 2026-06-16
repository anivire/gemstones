package name.modid.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@Mixin(HeldItemRenderer.class)
public class BowPullSpeedHeldItemRenderer {
  @ModifyConstant(method = "renderFirstPersonItem", constant = @Constant(floatValue = 20.0F), require = 0)
  private float gemstones$adjustBowPullAnimationTime(
      float pullTime,
      AbstractClientPlayerEntity player,
      float tickDelta,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack item,
      float equipProgress,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light) {
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
