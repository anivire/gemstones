package name.modid.core.api.entities.renderFeatures;

import name.modid.Gemstones;
import name.modid.core.api.entities.RenderUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;

public class StunnedStarsFeature<T extends LivingEntity, M extends EntityModel<T>>
    extends FeatureRenderer<T, M> {

  private static final Identifier SPRITE_STAR_BIG = Identifier.of(Gemstones.MOD_ID,
      "textures/world_particle/stunned_star.png");

  public StunnedStarsFeature(FeatureRendererContext<T, M> context) {
    super(context);
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
      T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress,
      float headYaw, float headPitch) {
    if (entity.getAttributes().getValue(EntityAttributes.GENERIC_JUMP_STRENGTH) == 0.0) {
      RenderUtils.renderCubeSprite(SPRITE_STAR_BIG, entity, matrices, vertexConsumers, light,
          tickDelta);
    }
  }
}
