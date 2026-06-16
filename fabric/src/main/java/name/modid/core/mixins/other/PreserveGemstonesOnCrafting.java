package name.modid.core.mixins.other;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;

@Mixin(CraftingScreenHandler.class)
public abstract class PreserveGemstonesOnCrafting {
  @Inject(method = "updateResult", at = @At("TAIL"))
  private static void onUpdateResult(
      net.minecraft.screen.ScreenHandler handler,
      net.minecraft.world.World world,
      net.minecraft.entity.player.PlayerEntity player,
      RecipeInputInventory craftingInventory,
      CraftingResultInventory resultInventory,
      net.minecraft.recipe.RecipeEntry<net.minecraft.recipe.CraftingRecipe> recipe,
      CallbackInfo ci) {
    ItemStack result = resultInventory.getStack(0);
    if (result.isEmpty()) return;
    if (!GemstoneSlotHelper.isItemValid(result.getItem())) return;

    for (int i = 0; i < craftingInventory.size(); i++) {
      ItemStack stack = craftingInventory.getStack(i);
      if (!stack.isEmpty() && GemstoneSlotHelper.isGemstonesExists(stack)) {
        GemstoneSlotHelper.copyGemstones(stack, result);
        return;
      }
    }
  }
}
