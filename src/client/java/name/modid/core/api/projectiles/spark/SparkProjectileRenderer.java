package name.modid.core.api.projectiles.spark;

import name.modid.Gemstones;
import name.modid.core.content.entities.SparkProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class SparkProjectileRenderer extends EntityRenderer<SparkProjectileEntity> {
  private static final int OUTLINE_COLOR = 0xFFFFB62A;

  protected SparkProjectileModel model;

  public SparkProjectileRenderer(EntityRendererFactory.Context ctx) {
    super(ctx);
    this.model = new SparkProjectileModel(ctx.getPart(SparkProjectileModel.SPARK));
  }

  @Override
  public Identifier getTexture(SparkProjectileEntity entity) {
    return Identifier.of(Gemstones.MOD_ID, "textures/entity/spark/spark.png");
  }

  @Override
  public void render(
      SparkProjectileEntity entity,
      float yaw,
      float tickDelta,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light) {

    matrices.push();
    matrices.scale(0.75f, 0.75f, 0.75f);

    float spin = (entity.age + tickDelta);
    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spin * 20.0f));
    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(spin * 9.4f));
    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(spin * 16.0f));

    VertexConsumer mainVc = vertexConsumers.getBuffer(
        RenderLayer.getEntityCutoutNoCull(getTexture(entity)));
    model.bb_main.render(matrices, mainVc, light, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);

    VertexConsumer outlineVc = vertexConsumers.getBuffer(
        RenderLayer.getEyes(getTexture(entity)));
    model.bb_outline.render(matrices, outlineVc, 0xF000F0, OverlayTexture.DEFAULT_UV, OUTLINE_COLOR);

    matrices.pop();

    super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
  }
}
