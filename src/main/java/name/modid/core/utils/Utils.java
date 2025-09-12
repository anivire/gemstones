package name.modid.core.utils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class Utils {
  public static ItemStack getSmeltingResult(World world, ItemStack input) {
    Optional<RecipeEntry<SmeltingRecipe>> recipeEntry = world.getRecipeManager()
        .getFirstMatch(RecipeType.SMELTING, new SingleStackRecipeInput(input), world);

    return recipeEntry
        .map(entry -> entry.value().getResult(world.getRegistryManager()))
        .orElse(ItemStack.EMPTY);
  }

  public static <R> List<R> collectPlayerArmorValues(
      ServerPlayerEntity player,
      Function<ItemStack, List<R>> callback) {
    return Stream.of(
        player.getEquippedStack(EquipmentSlot.HEAD),
        player.getEquippedStack(EquipmentSlot.CHEST),
        player.getEquippedStack(EquipmentSlot.LEGS),
        player.getEquippedStack(EquipmentSlot.FEET))
        .filter(stack -> !stack.isEmpty())
        .map(callback)
        .flatMap(List::stream)
        .toList();
  }
}