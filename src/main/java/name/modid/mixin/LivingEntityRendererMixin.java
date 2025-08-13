package name.modid.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import name.modid.Gemstones;
import name.modid.RenderUtils;
import name.modid.entities.EffectRegistrationHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
  private static final Identifier SPRITE_STAR_BIG =
      Identifier.of(Gemstones.MOD_ID, "textures/world_particle/stunned_star.png");

  @Inject(method = "render", at = @At("TAIL"))
  private void render(LivingEntity entity, float yaw, float tickDelta, MatrixStack matrices,
      VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
    System.out.println(entity.hasStatusEffect(EffectRegistrationHelper.STUNNED_EFFECT));
    if (entity.hasStatusEffect(EffectRegistrationHelper.STUNNED_EFFECT)) {
      RenderUtils.renderSprite(SPRITE_STAR_BIG, entity, matrices, vertexConsumers, light,
          tickDelta);
    }
  }
}
