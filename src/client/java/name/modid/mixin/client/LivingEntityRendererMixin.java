package name.modid.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import name.modid.Gemstones;
import name.modid.RenderUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

  private static final Identifier SPRITE_STAR_BIG =
      Identifier.of(Gemstones.MOD_ID, "textures/world_particle/stunned_star.png");

  @Inject(method = "render", at = @At("TAIL"))
  private void render(LivingEntity entity, float yaw, float tickDelta, MatrixStack matrices,
      VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
    System.out.println(entity.getAttributes().getValue(EntityAttributes.GENERIC_JUMP_STRENGTH));

    if (entity.getAttributes().getValue(EntityAttributes.GENERIC_JUMP_STRENGTH) == 0.0) {
      RenderUtils.renderSprite(SPRITE_STAR_BIG, entity, matrices, vertexConsumers, light,
          tickDelta);
    }
  }

  // @Invoker("addFeature")
  // public abstract boolean invokeAddFeature(FeatureRenderer<?, ?> feature);

  // @Inject(at = @At("RETURN"), method = "<init>")
  // public void init(EntityRendererFactory.Context ctx, EntityModel<?> model, float shadowRadius,
  // CallbackInfo info) {
  // this.invokeAddFeature(new StunnedFeature<>((FeatureRendererContext) (Object) this));
  // }
}
