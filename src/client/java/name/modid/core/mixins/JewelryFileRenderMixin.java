package name.modid.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.content.items.GemstoneItem;
import name.modid.core.content.items.tools.JewelryFileItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Mixin(HeldItemRenderer.class)
public abstract class JewelryFileRenderMixin {

  @Shadow
  public abstract void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode,
      boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

  @Shadow
  protected abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);

  @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
  private void renderJewelryFilePolishing(AbstractClientPlayerEntity player, float tickDelta, float pitch,
      Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices,
      VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
    ItemStack gemStack = player.getOffHandStack();
    if (!isPolishing(player, gemStack))
      return;

    if (hand == Hand.OFF_HAND) {
      ci.cancel();
      return;
    }

    if (!(stack.getItem() instanceof JewelryFileItem))
      return;

    boolean leftHand = player.getMainArm() == Arm.LEFT;
    Arm arm = leftHand ? Arm.LEFT : Arm.RIGHT;
    int modifier = leftHand ? -1 : 1;
    float time = player.getItemUseTimeLeft() - tickDelta + 1.0F;

    ci.cancel();
    matrices.push();
    matrices.translate(0.02F, -0.18F, -0.42F);
    matrices.scale(0.5F, 0.5F, 0.5F);

    matrices.push();
    matrices.translate(-0.16F, 0.02F, 0.02F);
    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(modifier * 35));
    float bobbing = -MathHelper.abs(MathHelper.cos(time / 8.0F * (float) Math.PI) * 0.025F);
    matrices.translate(0.0F, bobbing, 0.0F);
    matrices.scale(0.62F, 0.62F, 0.62F);
    renderItem(player, gemStack, ModelTransformationMode.GUI, leftHand, matrices, vertexConsumers, light);
    matrices.pop();

    float fileRubX = MathHelper.sin(time / 4.0F * (float) Math.PI) * 0.075F;
    float fileRubY = MathHelper.sin(time / 7.0F * (float) Math.PI + 0.8F) * 0.028F;
    float fileTiltZ = MathHelper.sin(time / 6.0F * (float) Math.PI) * 3.0F;
    float fileTiltX = MathHelper.sin(time / 9.0F * (float) Math.PI + 1.4F) * 2.0F;
    float fileTiltY = MathHelper.cos(time / 8.0F * (float) Math.PI) * 2.5F;
    matrices.translate(0.16F + fileRubX, fileRubY, -0.08F);
    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(modifier * (40 + fileTiltZ)));
    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(modifier * 10 + fileTiltX));
    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(modifier * (90 + fileTiltY)));
    matrices.scale(0.78F, 0.78F, 0.78F);
    renderItem(player, stack, ModelTransformationMode.NONE, leftHand, matrices, vertexConsumers, light);

    matrices.pop();
  }

  private boolean isPolishing(AbstractClientPlayerEntity player, ItemStack gemStack) {
    return player.isUsingItem()
        && player.getActiveHand() == Hand.MAIN_HAND
        && gemStack.contains(ComponentsRegistry.POLISHING)
        && gemStack.getItem() instanceof GemstoneItem;
  }
}
