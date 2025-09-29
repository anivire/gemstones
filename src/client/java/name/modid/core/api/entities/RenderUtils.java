package name.modid.core.api.entities;

import org.joml.Matrix4f;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class RenderUtils {
  public static void renderCubeSprite(Identifier sprite, LivingEntity entity, MatrixStack matrices,
      VertexConsumerProvider vertexConsumers, int light, float tickDelta) {
    matrices.push();
    matrices.translate(0.0F, entity.getHeight() - entity.getHeight() - entity.getHeight() * 0.5F,
        0.0F);

    float rotationSpeed = 10.0F;
    float rotationAngle = (entity.getWorld().getTime() * rotationSpeed + tickDelta * rotationSpeed) % 360;
    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationAngle));

    float spriteScale = 0.7F;
    matrices.scale(spriteScale, spriteScale, spriteScale);

    RenderLayer renderLayer = RenderLayer.getEntityTranslucent(sprite);
    VertexConsumer consumer = vertexConsumers.getBuffer(renderLayer);
    Matrix4f matrix = matrices.peek().getPositionMatrix();

    float half = 0.5F;
    float uMin = 0.0F;
    float vMin = 0.0F;
    float uMax = 1.0F;
    float vMax = 1.0F;

    // FRONT FACE (+Z)
    consumer.vertex(matrix, -half, -half, half).color(255, 255, 255, 255).texture(uMin, vMin)
        .overlay(0, 10).light(light).normal(0.0F, 0.0F, 1.0F);
    consumer.vertex(matrix, half, -half, half).color(255, 255, 255, 255).texture(uMax, vMin)
        .overlay(0, 10).light(light).normal(0.0F, 0.0F, 1.0F);
    consumer.vertex(matrix, half, half, half).color(255, 255, 255, 255).texture(uMax, vMax)
        .overlay(0, 10).light(light).normal(0.0F, 0.0F, 1.0F);
    consumer.vertex(matrix, -half, half, half).color(255, 255, 255, 255).texture(uMin, vMax)
        .overlay(0, 10).light(light).normal(0.0F, 0.0F, 1.0F);

    // BACK FACE (-Z)
    consumer.vertex(matrix, -half, half, -half).color(255, 255, 255, 255).texture(uMin, vMax)
        .overlay(0, 10).light(light).normal(0.0F, 0.0F, -1.0F);
    consumer.vertex(matrix, half, half, -half).color(255, 255, 255, 255).texture(uMax, vMax)
        .overlay(0, 10).light(light).normal(0.0F, 0.0F, -1.0F);
    consumer.vertex(matrix, half, -half, -half).color(255, 255, 255, 255).texture(uMax, vMin)
        .overlay(0, 10).light(light).normal(0.0F, 0.0F, -1.0F);
    consumer.vertex(matrix, -half, -half, -half).color(255, 255, 255, 255).texture(uMin, vMin)
        .overlay(0, 10).light(light).normal(0.0F, 0.0F, -1.0F);

    // LEFT FACE (-X)
    consumer.vertex(matrix, -half, -half, -half).color(255, 255, 255, 255).texture(uMin, vMin)
        .overlay(0, 10).light(light).normal(-1.0F, 0.0F, 0.0F);
    consumer.vertex(matrix, -half, -half, half).color(255, 255, 255, 255).texture(uMax, vMin)
        .overlay(0, 10).light(light).normal(-1.0F, 0.0F, 0.0F);
    consumer.vertex(matrix, -half, half, half).color(255, 255, 255, 255).texture(uMax, vMax)
        .overlay(0, 10).light(light).normal(-1.0F, 0.0F, 0.0F);
    consumer.vertex(matrix, -half, half, -half).color(255, 255, 255, 255).texture(uMin, vMax)
        .overlay(0, 10).light(light).normal(-1.0F, 0.0F, 0.0F);

    // RIGHT FACE (+X)
    consumer.vertex(matrix, half, half, -half).color(255, 255, 255, 255).texture(uMin, vMax)
        .overlay(0, 10).light(light).normal(1.0F, 0.0F, 0.0F);
    consumer.vertex(matrix, half, half, half).color(255, 255, 255, 255).texture(uMax, vMax)
        .overlay(0, 10).light(light).normal(1.0F, 0.0F, 0.0F);
    consumer.vertex(matrix, half, -half, half).color(255, 255, 255, 255).texture(uMax, vMin)
        .overlay(0, 10).light(light).normal(1.0F, 0.0F, 0.0F);
    consumer.vertex(matrix, half, -half, -half).color(255, 255, 255, 255).texture(uMin, vMin)
        .overlay(0, 10).light(light).normal(1.0F, 0.0F, 0.0F);

    matrices.pop();
  }

}
