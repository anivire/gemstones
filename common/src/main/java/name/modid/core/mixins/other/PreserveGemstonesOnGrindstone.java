package name.modid.core.mixins.other;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;

@Mixin(GrindstoneScreenHandler.class)
public abstract class PreserveGemstonesOnGrindstone {
  @Shadow
  Inventory input;

  @Inject(method = "updateResult", at = @At("TAIL"))
  private void onUpdateResult(CallbackInfo ci) {
    GrindstoneScreenHandler handler = (GrindstoneScreenHandler) (Object) this;
    ItemStack result = handler.getSlot(2).getStack();
    if (result.isEmpty()) return;

    ItemStack source = input.getStack(0);
    if (!source.isEmpty()) {
      GemstoneSlotHelper.copyGemstones(source, result);
    } else {
      source = input.getStack(1);
      if (!source.isEmpty()) {
        GemstoneSlotHelper.copyGemstones(source, result);
      }
    }
  }
}
