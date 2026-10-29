package name.modid.core.mixins.other;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import name.modid.core.content.items.GemstoneDustItem;
import net.minecraft.item.ItemStack;

// Both grindstone input slots are anonymous classes
@Mixin(targets = {
    "net.minecraft.screen.GrindstoneScreenHandler$2",
    "net.minecraft.screen.GrindstoneScreenHandler$3"
})
public abstract class GrindstoneAcceptGemstones {
  @ModifyReturnValue(method = "canInsert", at = @At("RETURN"))
  private boolean gemstones$acceptGemstones(boolean original, @Local(argsOnly = true) ItemStack stack) {
    return original || GemstoneDustItem.isGrindable(stack);
  }
}
