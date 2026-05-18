package name.modid.core.api.features;

import org.joml.Quaternionf;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public final class SoulburnFireRenderer {
  private static final Identifier SOUL_FIRE_0_ID = Identifier.of("minecraft", "block/soul_fire_0");
  private static final Identifier SOUL_FIRE_1_ID = Identifier.of("minecraft", "block/soul_fire_1");

  private SoulburnFireRenderer() {
  }

  public static void render(MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      Entity entity,
      Quaternionf rotation) {

    @SuppressWarnings("deprecation")
    Sprite sprite = MinecraftClient.getInstance()
        .getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
        .apply(SOUL_FIRE_0_ID);

    @SuppressWarnings("deprecation")
    Sprite sprite2 = MinecraftClient.getInstance()
        .getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
        .apply(SOUL_FIRE_1_ID);

    matrices.push();

    float f = entity.getWidth() * 1.4F;
    matrices.scale(f, f, f);
    float g = 0.5F;
    float i = entity.getHeight() / f;
    float j = 0.0F;

    matrices.multiply(rotation);
    matrices.translate(0.0F, 0.0F, 0.3F - (int) i * 0.02F);

    float k = 0.0F;
    int l = 0;

    VertexConsumer vc = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());

    for (MatrixStack.Entry entry = matrices.peek(); i > 0.0F; l++) {
      Sprite sprite3 = l % 2 == 0 ? sprite : sprite2;
      float minU = sprite3.getMinU();
      float minV = sprite3.getMinV();
      float maxU = sprite3.getMaxU();
      float maxV = sprite3.getMaxV();

      if (l / 2 % 2 == 0) {
        float tmp = maxU;
        maxU = minU;
        minU = tmp;
      }

      drawFireVertex(entry, vc, -g, 0.0F - j, k, maxU, maxV);
      drawFireVertex(entry, vc, g, 0.0F - j, k, minU, maxV);
      drawFireVertex(entry, vc, g, 1.4F - j, k, minU, minV);
      drawFireVertex(entry, vc, -g, 1.4F - j, k, maxU, minV);

      i -= 0.45F;
      j -= 0.45F;
      g *= 0.9F;
      k -= 0.03F;
    }

    matrices.pop();
  }

  private static void drawFireVertex(MatrixStack.Entry entry,
      VertexConsumer vertices,
      float x, float y, float z,
      float u, float v) {
    vertices.vertex(entry, x, y, z)
        .color(Colors.WHITE)
        .texture(u, v)
        .overlay(0, 10)
        .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE)
        .normal(entry, 0.0F, 1.0F, 0.0F);
  }
}
