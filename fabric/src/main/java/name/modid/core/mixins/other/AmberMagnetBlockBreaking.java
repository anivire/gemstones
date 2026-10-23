package name.modid.core.mixins.other;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.utils.amber.AmberMagnetHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;

// Amber tools magnet: everything dropped during a block break by the player goes to the inventory
@Mixin(ServerPlayerInteractionManager.class)
public class AmberMagnetBlockBreaking {
  @Shadow
  @Final
  protected ServerPlayerEntity player;

  @Inject(method = "tryBreakBlock", at = @At("HEAD"))
  private void beginMagnetBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
    AmberMagnetHelper.beginBreak(this.player);
  }

  @Inject(method = "tryBreakBlock", at = @At("RETURN"))
  private void endMagnetBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
    AmberMagnetHelper.endBreak();
  }
}
