package name.modid.datagen.providers;

import java.util.concurrent.CompletableFuture;

import name.modid.core.content.blocks.BlocksRegistry;
import name.modid.core.content.items.registries.ItemsRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

public class RecipeProvider extends FabricRecipeProvider {

  public RecipeProvider(
      FabricDataOutput output,
      CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  public void generate(RecipeExporter exporter) {
    ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, BlocksRegistry.JEWELRY_TABLE)
        .pattern("DG ")
        .pattern("PP ")
        .pattern("PP ")
        .input('D', Items.DIAMOND)
        .input('G', Items.GLASS)
        .input('P', ItemTags.PLANKS)
        .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
        .offerTo(exporter);

    ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemsRegistry.DIAMOND_TIPPED_CHISEL)
        .pattern(" D ")
        .pattern(" I ")
        .pattern(" S ")
        .input('D', Items.DIAMOND)
        .input('I', Items.IRON_INGOT)
        .input('S', Items.STICK)
        .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
        .offerTo(exporter);

    ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemsRegistry.NETHERITE_TIPPED_CHISEL)
        .pattern(" N ")
        .pattern(" I ")
        .pattern(" S ")
        .input('N', Items.NETHERITE_INGOT)
        .input('I', Items.GOLD_INGOT)
        .input('S', Items.STICK)
        .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
        .offerTo(exporter);

    ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemsRegistry.JEWELRY_PLIERS)
        .pattern("G G")
        .pattern(" I ")
        .pattern(" S ")
        .input('I', Items.IRON_INGOT)
        .input('G', Items.GOLD_INGOT)
        .input('S', Items.STICK)
        .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
        .offerTo(exporter);

    ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemsRegistry.JEWELRY_HAMMER)
        .pattern("GIG")
        .pattern(" S ")
        .pattern(" S ")
        .input('I', Items.IRON_INGOT)
        .input('G', Items.GOLD_INGOT)
        .input('S', Items.STICK)
        .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
        .offerTo(exporter);

    ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ItemsRegistry.EXPANSION_CRYSTAL)
        .pattern("SS ")
        .pattern("DS ")
        .input('S', Items.AMETHYST_SHARD)
        .input('D', Items.DIAMOND)
        .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
        .offerTo(exporter);
  }
}
