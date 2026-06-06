package name.modid.core.mixins.modifiers;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import name.modid.core.api.modifiers.config.utils.PotionUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.entry.RegistryEntry;

@Mixin(BrewingRecipeRegistry.class)
public abstract class BrewCustomDurationPotions {
  @ModifyVariable(method = "hasPotionRecipe", at = @At("STORE"), ordinal = 0)
  private Optional<RegistryEntry<Potion>> useCustomDurationBasePotionForRecipeCheck(
      Optional<RegistryEntry<Potion>> potion,
      ItemStack input,
      ItemStack ingredient) {
    return potion.isPresent() ? potion : PotionUtils.getBrewablePotion(input);
  }

  @ModifyVariable(method = "craft", at = @At("STORE"), ordinal = 0)
  private Optional<RegistryEntry<Potion>> useCustomDurationBasePotionForCraft(
      Optional<RegistryEntry<Potion>> potion,
      ItemStack ingredient,
      ItemStack input) {
    return potion.isPresent() ? potion : PotionUtils.getBrewablePotion(input);
  }
}
