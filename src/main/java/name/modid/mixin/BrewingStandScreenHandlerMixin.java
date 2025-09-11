package name.modid.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.utils.accessors.BrewingStandBlockEntityAccess;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.BrewingStandScreenHandler;

@Mixin(BrewingStandScreenHandler.class)
public abstract class BrewingStandScreenHandlerMixin {

  @Inject(method = "canUse", at = @At("HEAD"))
  private void onCanUse(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
    Inventory inv = ((BrewingStandScreenHandlerAccessor) this).getInventory();
    if (inv instanceof BrewingStandBlockEntity stand) {
      ((BrewingStandBlockEntityAccess) stand).setLastBrewer(player.getUuid());
    }
  }
}