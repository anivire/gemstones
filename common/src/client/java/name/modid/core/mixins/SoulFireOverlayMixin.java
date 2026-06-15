package name.modid.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.utils.accessors.SoulBurnEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Mixin(InGameOverlayRenderer.class)
public class SoulFireOverlayMixin {

  @Unique
  private static final SpriteIdentifier SOUL_FIRE_1 = new SpriteIdentifier(
      SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
      Identifier.of("minecraft", "block/soul_fire_1"));

  @Shadow
  private static void renderFireOverlay(MinecraftClient client, MatrixStack matrices) {
    throw new AssertionError();
  }

  @Inject(method = "renderOverlays", at = @At("TAIL"))
  private static void renderSoulFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
    PlayerEntity player = client.player;
    if (player == null)
      return;
    if (player.isOnFire())
      return;
    if (!((SoulBurnEntityAccessor) player).hasSoulBurnEffect())
      return;

    renderFireOverlay(client, matrices);
  }

  @Redirect(method = "renderFireOverlay", at = @At(value = "FIELD",
      target = "Lnet/minecraft/client/render/model/ModelLoader;FIRE_1:Lnet/minecraft/client/util/SpriteIdentifier;"))
  private static SpriteIdentifier useSoulburnFireForOverlay() {
    PlayerEntity player = MinecraftClient.getInstance().player;
    if (player != null && ((SoulBurnEntityAccessor) player).hasSoulBurnEffect()) {
      return SOUL_FIRE_1;
    }
    return ModelLoader.FIRE_1;
  }
}
