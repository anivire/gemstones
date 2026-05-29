package name.modid.core.mixins.other;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SmithingScreenHandler;

@Mixin(SmithingScreenHandler.class)
public abstract class PreserveGemstonesOnSmithing {
  @Inject(method = "updateResult", at = @At("TAIL"))
  private void onUpdateResult(CallbackInfo ci) {
    SmithingScreenHandler handler = (SmithingScreenHandler) (Object) this;
    ItemStack base = handler.getSlot(1).getStack();
    ItemStack result = handler.getSlot(3).getStack();
    GemstoneSlotHelper.copyGemstones(base, result);
  }
}
