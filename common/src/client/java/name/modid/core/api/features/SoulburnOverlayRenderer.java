package name.modid.core.api.features;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public final class SoulburnOverlayRenderer {
  private static final Identifier SOUL_FIRE_1_ID = Identifier.of("minecraft", "block/soul_fire_1");

  private SoulburnOverlayRenderer() {
  }

  public static void render(MatrixStack matrices) {
    @SuppressWarnings("deprecation")
    Sprite sprite = MinecraftClient.getInstance()
        .getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
        .apply(SOUL_FIRE_1_ID);

    RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
    RenderSystem.depthFunc(519);
    RenderSystem.depthMask(false);
    RenderSystem.enableBlend();
    RenderSystem.setShaderTexture(0, sprite.getAtlasId());

    float minU = sprite.getMinU();
    float maxU = sprite.getMaxU();
    float centerU = (minU + maxU) / 2.0F;
    float minV = sprite.getMinV();
    float maxV = sprite.getMaxV();
    float centerV = (minV + maxV) / 2.0F;
    float animationDelta = sprite.getAnimationFrameDelta();
    float animatedMinU = MathHelper.lerp(animationDelta, minU, centerU);
    float animatedMaxU = MathHelper.lerp(animationDelta, maxU, centerU);
    float animatedMinV = MathHelper.lerp(animationDelta, minV, centerV);
    float animatedMaxV = MathHelper.lerp(animationDelta, maxV, centerV);

    for (int side = 0; side < 2; side++) {
      matrices.push();
      matrices.translate(-((side * 2 - 1)) * 0.24F, -0.3F, 0.0F);
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((side * 2 - 1) * 10.0F));
      Matrix4f matrix = matrices.peek().getPositionMatrix();
      BufferBuilder buffer = Tessellator.getInstance()
          .begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      buffer.vertex(matrix, -0.5F, -0.5F, -0.5F)
          .texture(animatedMaxU, animatedMaxV).color(1.0F, 1.0F, 1.0F, 0.9F);
      buffer.vertex(matrix, 0.5F, -0.5F, -0.5F)
          .texture(animatedMinU, animatedMaxV).color(1.0F, 1.0F, 1.0F, 0.9F);
      buffer.vertex(matrix, 0.5F, 0.5F, -0.5F)
          .texture(animatedMinU, animatedMinV).color(1.0F, 1.0F, 1.0F, 0.9F);
      buffer.vertex(matrix, -0.5F, 0.5F, -0.5F)
          .texture(animatedMaxU, animatedMinV).color(1.0F, 1.0F, 1.0F, 0.9F);

      BufferRenderer.drawWithGlobalProgram(buffer.end());
      matrices.pop();
    }

    RenderSystem.disableBlend();
    RenderSystem.depthMask(true);
    RenderSystem.depthFunc(515);
  }
}
