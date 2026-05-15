package name.modid.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.api.MultiMinerClientPreview;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MultiMinerMiningFeedbackMixin {
  @Shadow
  public abstract int getBlockBreakingProgress();

  @Shadow
  public abstract boolean isBreakingBlock();

  @Inject(method = "attackBlock", at = @At("TAIL"))
  private void gemstones$startMultiMinerMiningFeedback(BlockPos pos, Direction direction,
      CallbackInfoReturnable<Boolean> cir) {
    if (cir.getReturnValue()) {
      showMultiMinerMiningFeedback(pos, direction);
    }
  }

  @Inject(method = "updateBlockBreakingProgress", at = @At("TAIL"))
  private void gemstones$updateMultiMinerMiningFeedback(BlockPos pos, Direction direction,
      CallbackInfoReturnable<Boolean> cir) {
    if (cir.getReturnValue()) {
      showMultiMinerMiningFeedback(pos, direction);
    } else {
      clearMultiMinerMiningFeedback();
    }
  }

  @Inject(method = "cancelBlockBreaking", at = @At("HEAD"))
  private void gemstones$cancelMultiMinerMiningFeedback(CallbackInfo ci) {
    clearMultiMinerMiningFeedback();
  }

  @Inject(method = "breakBlock", at = @At("HEAD"))
  private void gemstones$clearMultiMinerMiningFeedbackOnBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
    clearMultiMinerMiningFeedback();
  }

  @Inject(method = "tick", at = @At("TAIL"))
  private void gemstones$tickMultiMinerMiningFeedback(CallbackInfo ci) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.player == null || client.world == null) {
      MultiMinerClientPreview.clearHighlights();
      return;
    }

    MultiMinerClientPreview.updateTargetHighlights(client.player, client.world, client.crosshairTarget);

    if (isBreakingBlock()) {
      MultiMinerClientPreview.clearStaleBreakingProgress(client.world);
    } else {
      MultiMinerClientPreview.clearBreakingProgress(client.world);
    }
  }

  private void showMultiMinerMiningFeedback(BlockPos pos, Direction direction) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.player == null || client.world == null) {
      return;
    }

    MultiMinerClientPreview.showMiningFeedback(
        client.player,
        client.world,
        pos,
        direction,
        getBlockBreakingProgress());
  }

  private void clearMultiMinerMiningFeedback() {
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world != null) {
      MultiMinerClientPreview.clearBreakingProgress(client.world);
    }
  }
}
