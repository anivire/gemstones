package name.modid.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.api.tooltips.QualityTooltipComponent;
import name.modid.core.api.tooltips.QualityTooltipData;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;

@Mixin(TooltipComponent.class)
public interface QualityTooltipComponentMixin {
  @Inject(method = "of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;", at = @At("HEAD"), cancellable = true)
  private static void gemstones$ofQualityTooltip(TooltipData data, CallbackInfoReturnable<TooltipComponent> cir) {
    if (data instanceof QualityTooltipData qualityData) {
      cir.setReturnValue(new QualityTooltipComponent(qualityData.qualities()));
    }
  }
}
