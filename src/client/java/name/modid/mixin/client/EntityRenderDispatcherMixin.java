package name.modid.mixin.client;

import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.utils.accessors.SoulBurnEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
  private static final Identifier SOUL_FIRE_0_ID = Identifier.of("minecraft", "block/soul_fire_0");
  private static final Identifier SOUL_FIRE_1_ID = Identifier.of("minecraft", "block/soul_fire_1");

  @Shadow
  private Quaternionf rotation;

  // TODO: move later to feature
  @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V", ordinal = 1, shift = At.Shift.BEFORE))
  private <E extends Entity> void renderCustomEffects(
      E entity, double x, double y, double z, float yaw, float tickDelta,
      MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
      CallbackInfo ci) {

    if (entity instanceof LivingEntity livingEntity && entity instanceof SoulBurnEntityAccessor soulBurnEntity) {
      if (soulBurnEntity.hasSoulBurnEffect()) {
        renderSoulFire(matrices, vertexConsumers, livingEntity,
            MathHelper.rotateAround(MathHelper.Y_AXIS, this.rotation, new Quaternionf()));
      }
    }
  }

  private void renderSoulFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity,
      Quaternionf rotation) {
    @SuppressWarnings("deprecation")
    Sprite sprite = MinecraftClient.getInstance()
        .getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
        .apply(SOUL_FIRE_0_ID);

    @SuppressWarnings("deprecation")
    Sprite sprite2 = MinecraftClient.getInstance()
        .getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
        .apply(SOUL_FIRE_1_ID);

    // Vanilla like-render
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
    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());

    for (MatrixStack.Entry entry = matrices.peek(); i > 0.0F; l++) {
      Sprite sprite3 = l % 2 == 0 ? sprite : sprite2;
      float m = sprite3.getMinU();
      float n = sprite3.getMinV();
      float o = sprite3.getMaxU();
      float p = sprite3.getMaxV();
      if (l / 2 % 2 == 0) {
        float q = o;
        o = m;
        m = q;
      }

      drawFireVertex(entry, vertexConsumer, -g - 0.0F, 0.0F - j, k, o, p);
      drawFireVertex(entry, vertexConsumer, g - 0.0F, 0.0F - j, k, m, p);
      drawFireVertex(entry, vertexConsumer, g - 0.0F, 1.4F - j, k, m, n);
      drawFireVertex(entry, vertexConsumer, -g - 0.0F, 1.4F - j, k, o, n);
      i -= 0.45F;
      j -= 0.45F;
      g *= 0.9F;
      k -= 0.03F;
    }

    matrices.pop();
  }

  private static void drawFireVertex(MatrixStack.Entry entry, VertexConsumer vertices, float x, float y, float z,
      float u, float v) {
    vertices.vertex(entry, x, y, z)
        .color(Colors.WHITE)
        .texture(u, v)
        .overlay(0, 10)
        .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE)
        .normal(entry, 0.0F, 1.0F, 0.0F);
  }
}