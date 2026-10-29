package name.modid.core.mixins.other;

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
    ItemStack gem = gemstones$getGrindingGem();
    if (gem.isEmpty()) {
      return output;
    }

    return new ItemStack(ItemsRegistry.gemstoneDust(), GemstoneDustItem.getDustYield(gem));
  }

  @Override
  public ItemStack gemstones$getGrindingGem() {
    ItemStack first = input.getStack(0);
    ItemStack second = input.getStack(1);
    if (!first.isEmpty() && !second.isEmpty()) {
      return ItemStack.EMPTY;
    }

    ItemStack gem = first.isEmpty() ? second : first;
    return GemstoneDustItem.isGrindable(gem) ? gem : ItemStack.EMPTY;
  }
}
