package name.modid.core.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import name.modid.core.api.tooltips.SocketsTooltip;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Mixin(ItemStack.class)
public class SocketedItemsTooltip {
  // Wraps the first add (item name) so the sockets section goes right below it,
  // without capturing locals that other mods may shift around
  @WrapOperation(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
  private boolean gemstones$appendSockets(List<Text> tooltip, Object line, Operation<Boolean> original) {
    boolean added = original.call(tooltip, line);
    SocketsTooltip.appendTo((ItemStack) (Object) this, tooltip);
    return added;
  }
}
