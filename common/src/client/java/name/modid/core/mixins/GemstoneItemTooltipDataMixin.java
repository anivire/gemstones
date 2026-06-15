package name.modid.core.mixins;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.content.items.GemstoneItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

@Mixin(GemstoneItem.class)
public class GemstoneItemTooltipDataMixin {
  @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
  private void gemstones$hideQualityDataOutsideScreens(ItemStack stack,
      CallbackInfoReturnable<Optional<TooltipData>> cir) {
    if (MinecraftClient.getInstance().currentScreen == null) {
      cir.setReturnValue(Optional.empty());
    }
  }
}
