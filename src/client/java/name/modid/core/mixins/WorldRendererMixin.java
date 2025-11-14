package name.modid.core.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.BlockBreakConfig;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

  @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
  private void drawMergedCrossOutline(MatrixStack matrices, VertexConsumer vertexConsumer,
      Entity entity, double camX, double camY, double camZ,
      BlockPos blockPos, BlockState blockState,
      CallbackInfo ci) {
    if (!(entity instanceof PlayerEntity player))
      return;

    List<GemstoneModifier> modifiers = ModifierGatheringHelper.getModifiers(
        player.getMainHandStack(), BlockBreakConfig.class);

    if (modifiers.stream()
        .noneMatch(x -> x.getConfig() instanceof BlockBreakConfig c
            && c.eventType() == EventType.ON_BLOCK_BREAK_MINER))
      return;

    HitResult hit = player.raycast(5.0, 0, false);
    if (!(hit instanceof BlockHitResult blockHit))
      return;

    Direction face = blockHit.getSide();
    int minX = 0, minY = 0, minZ = 0;
    int maxX = 0, maxY = 0, maxZ = 0;

    switch (face.getAxis()) {
      case Y -> {
        minX = blockPos.getX() - 1;
        maxX = blockPos.getX() + 1;
        minY = blockPos.getY();
        maxY = blockPos.getY();
        minZ = blockPos.getZ() - 1;
        maxZ = blockPos.getZ() + 1;
      }
      case X -> {
        minX = blockPos.getX();
        maxX = blockPos.getX();
        minY = blockPos.getY() - 1;
        maxY = blockPos.getY() + 1;
        minZ = blockPos.getZ() - 1;
        maxZ = blockPos.getZ() + 1;
      }
      case Z -> {
        minX = blockPos.getX() - 1;
        maxX = blockPos.getX() + 1;
        minY = blockPos.getY() - 1;
        maxY = blockPos.getY() + 1;
        minZ = blockPos.getZ();
        maxZ = blockPos.getZ();
      }
    }

    double minDx = minX - camX;
    double minDy = minY - camY;
    double minDz = minZ - camZ;
    double maxDx = maxX + 1 - camX;
    double maxDy = maxY + 1 - camY;
    double maxDz = maxZ + 1 - camZ;

    Box box = new Box(minDx, minDy, minDz, maxDx, maxDy, maxDz).expand(0.002D);
    WorldRenderer.drawBox(
        matrices, vertexConsumer,
        box.minX, box.minY, box.minZ,
        box.maxX, box.maxY, box.maxZ,
        0.0F, 0.0F, 0.0F, 0.4F);

    ci.cancel();
  }
}