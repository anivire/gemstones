package name.modid.utils;

import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.world.World;

public class Utils {
  public static ItemStack getSmeltingResult(World world, ItemStack input) {
    Optional<RecipeEntry<SmeltingRecipe>> recipeEntry = world.getRecipeManager()
        .getFirstMatch(RecipeType.SMELTING, new SingleStackRecipeInput(input), world);

    return recipeEntry
        .map(entry -> entry.value().getResult(world.getRegistryManager()))
        .orElse(ItemStack.EMPTY);
  }
}