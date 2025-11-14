package name.modid.core.api.projectiles.spark; // Убедитесь, что пакет верный

import name.modid.Gemstones; // Ваш ID мода
import name.modid.core.content.entities.SparkProjectileEntity; // Ваш класс сущности
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SparkProjectileModel extends EntityModel<SparkProjectileEntity> {
  public static final EntityModelLayer SPARK = new EntityModelLayer(
      Identifier.of(Gemstones.MOD_ID, "spark_projectile"), "main");

  public final ModelPart bb_main;
  public final ModelPart bb_outline;

  public SparkProjectileModel(ModelPart root) {
    this.bb_main = root.getChild("bb_main");
    this.bb_outline = root.getChild("bb_outline");
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();

    modelPartData.addChild("bb_main",
        ModelPartBuilder.create()
            .uv(0, 0)
            .cuboid(-3.0F, -4.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)),
        ModelTransform.pivot(0.0F, 0.0F, 0.0F));

    modelPartData.addChild("bb_outline",
        ModelPartBuilder.create()
            .uv(0, 12)
            .cuboid(-4.0F, -5.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)),
        ModelTransform.pivot(0.0F, 0.0F, 0.0F));

    return TexturedModelData.of(modelData, 32, 32);
  }

  @Override
  public void setAngles(SparkProjectileEntity entity, float limbAngle, float limbDistance, float animationProgress,
      float headYaw, float headPitch) {
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
  }
}