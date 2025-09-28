package name.modid.core.api.projectiles.spark;

import name.modid.Gemstones;
import name.modid.core.content.entities.SparkProjectileEntity;
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
  public static final EntityModelLayer SPARK = new EntityModelLayer(Identifier.of(Gemstones.MOD_ID, "spark"), "main");
  private final ModelPart root;

  public SparkProjectileModel(ModelPart root) {
    this.root = root.getChild("root");
  }

  public static TexturedModelData getTexturedModelData() {
    ModelData modelData = new ModelData();
    ModelPartData modelPartData = modelData.getRoot();
    ModelPartData root = modelPartData.addChild(
        "root",
        ModelPartBuilder.create()
            .uv(0, 0)
            .cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F),
        ModelTransform.pivot(0.0F, 0.0F, 0.0F));
    return TexturedModelData.of(modelData, 8, 4);
  }

  @Override
  public void setAngles(SparkProjectileEntity entity, float limbAngle, float limbDistance, float animationProgress,
      float headYaw, float headPitch) {
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
    root.render(matrices, vertices, light, overlay, color);
  }
}
