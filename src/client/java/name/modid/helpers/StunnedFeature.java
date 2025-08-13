package name.modid.helpers;

import name.modid.Gemstones;
import name.modid.RenderUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class StunnedFeature<T extends LivingEntity, M extends net.minecraft.client.render.entity.model.EntityModel<T>>
    extends FeatureRenderer<T, M> {

  private static final Identifier SPRITE_STAR_BIG =
      Identifier.of(Gemstones.MOD_ID, "textures/world_particle/stunned_star.png");

  public StunnedFeature(FeatureRendererContext<T, M> context) {
    super(context);
  }


  @Override
  public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
      T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress,
      float headYaw, float headPitch) {

    System.out.println(entity.getStatusEffects().toString());
    // if (entity.hasStatusEffect(EffectRegistrationHelper.STUNNED_EFFECT)) {
    RenderUtils.renderSprite(SPRITE_STAR_BIG, entity, matrices, vertexConsumers, light, tickDelta);
    // }
  }
}
