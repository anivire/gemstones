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
    matrices.scale(2.0f, 2.0f, 2.0f);

    float spin = (entity.age + tickDelta);
    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spin * 40.0f));
    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(spin * 18.8f));
    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(spin * 33.2f));

    VertexConsumer vc = vertexConsumers.getBuffer(
        RenderLayer.getEntityCutoutNoCull(getTexture(entity)));

    this.model.setAngles(entity, 0, 0, 0, 0, 0);
    this.model.render(matrices, vc, light, OverlayTexture.DEFAULT_UV, 0xFFFFFF);

    matrices.pop();

    super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
  }
}