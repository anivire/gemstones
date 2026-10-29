package name.modid.core.mixins.other;

import java.util.ArrayList;
import java.util.List;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import name.modid.core.content.items.GemstoneDustItem;
import name.modid.core.content.items.registries.ItemsRegistry;
import name.modid.core.utils.accessors.GrindstoneGemAccessor;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;

// One gemstone is ground per take, the rest of the stack stays in the slot
@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindGemstonesIntoDust implements GrindstoneGemAccessor {
  @Shadow
  @Final
  Inventory input;

  @ModifyReturnValue(method = "getOutputStack", at = @At("RETURN"))
  private ItemStack gemstones$grindGemIntoDust(ItemStack output) {
    List<ItemStack> gems = gemstones$getGrindingGems();
    if (gems.isEmpty()) {
      boolean hasGem = GemstoneDustItem.isGrindable(input.getStack(0))
          || GemstoneDustItem.isGrindable(input.getStack(1));
      return hasGem ? ItemStack.EMPTY : output;
    }

    int dust = gems.stream().mapToInt(GemstoneDustItem::getDustYield).sum();
    return new ItemStack(ItemsRegistry.gemstoneDust(), dust);
  }

  @Override
  public List<ItemStack> gemstones$getGrindingGems() {
    List<ItemStack> gems = new ArrayList<>();
    for (ItemStack stack : List.of(input.getStack(0), input.getStack(1))) {
      if (stack.isEmpty()) {
        continue;
      }
      if (!GemstoneDustItem.isGrindable(stack)) {
        return List.of();
      }
      gems.add(stack);
    }

    return gems;
  }
}
