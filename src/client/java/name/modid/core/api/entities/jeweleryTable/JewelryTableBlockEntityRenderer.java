package name.modid.core.api.entities.jeweleryTable;

import name.modid.core.content.blocks.entity.JewelryTableBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class JewelryTableBlockEntityRenderer implements BlockEntityRenderer<JewelryTableBlockEntity> {

  public JewelryTableBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
  }

  @Override
  public void render(JewelryTableBlockEntity entity, float tickDelta, MatrixStack matrices,
      VertexConsumerProvider vertexConsumers, int light, int overlay) {

    ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

    ItemStack stackLeft = entity.getStack(0);
    ItemStack stackRight = entity.getStack(1);

    World world = entity.getWorld();
    BlockPos pos = entity.getPos();

    int lightLevel = getLightLevel(world, pos);

    matrices.push();
    matrices.translate(0.5f, 1.02f, 0.5f);

    if (!stackLeft.isEmpty()) {
      matrices.push();
      matrices.translate(-0.2f, 0, 0);
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
      matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45f));
      matrices.scale(0.5f, 0.5f, 0.5f);
      itemRenderer.renderItem(
          stackLeft,
          ModelTransformationMode.GUI,
          lightLevel,
          OverlayTexture.DEFAULT_UV,
          matrices,
          vertexConsumers,
          world,
          1);
      matrices.pop();
    }

    if (!stackRight.isEmpty()) {
      matrices.push();
      matrices.translate(0.2f, 0, 0);
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
      matrices.scale(0.5f, 0.5f, 0.5f);
      itemRenderer.renderItem(
          stackRight,
          ModelTransformationMode.GUI,
          lightLevel,
          OverlayTexture.DEFAULT_UV,
          matrices,
          vertexConsumers,
          world,
          1);
      matrices.pop();
    }

    matrices.pop();
  }

  private int getLightLevel(World world, BlockPos pos) {
    int bLight = world.getLightLevel(LightType.BLOCK, pos);
    int sLight = world.getLightLevel(LightType.SKY, pos);
    return LightmapTextureManager.pack(bLight, sLight);
  }
}