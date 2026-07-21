package name.modid.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.features.SoulburnOverlayRenderer;
import name.modid.core.utils.accessors.SoulBurnEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(InGameOverlayRenderer.class)
public class SoulFireOverlayMixin {
  @Inject(method = "renderOverlays", at = @At("TAIL"))
  private static void renderSoulFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
    PlayerEntity player = client.player;
    if (player == null)
      return;
    if (player.isOnFire())
      return;
    if (!((SoulBurnEntityAccessor) player).hasSoulBurnEffect())
      return;

    SoulburnOverlayRenderer.render(matrices);
  }
}
