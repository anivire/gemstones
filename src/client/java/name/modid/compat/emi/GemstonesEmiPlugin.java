package name.modid.compat.emi;

import java.util.List;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import name.modid.compat.JewelryTableRecipeExamples;
import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.content.blocks.BlocksRegistry;
import name.modid.core.content.items.GemstoneItem;
import name.modid.core.content.items.registries.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class GemstonesEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(JewelryTableEmiRecipeCategory.INSERT);
        registry.addCategory(JewelryTableEmiRecipeCategory.REMOVE);
        registry.addCategory(JewelryTableEmiRecipeCategory.EXPAND);

        EmiStack workstation = EmiStack.of(BlocksRegistry.JEWELRY_TABLE);
        registry.addWorkstation(JewelryTableEmiRecipeCategory.INSERT, workstation);
        registry.addWorkstation(JewelryTableEmiRecipeCategory.REMOVE, workstation);
        registry.addWorkstation(JewelryTableEmiRecipeCategory.EXPAND, workstation);

        List<Item> socketableItems = JewelryTableRecipeExamples.allSocketableItems();

        int idCounter = 0;

        for (ItemStack gem : JewelryTableRecipeExamples.allGemstoneStacks()) {
            List<ItemStack> bases = socketableItems.stream()
                    .map(JewelryTableRecipeExamples::baseWithEmptySocket)
                    .toList();
            List<ItemStack> results = socketableItems.stream()
                    .map(item -> {
                        ItemStack result = JewelryTableRecipeExamples.baseWithEmptySocket(item);
                        if (gem.getItem() instanceof GemstoneItem gemstone) {
                            GemstoneSlotHelper.setGemstoneByIndex(result, 0, gemstone);
                        }
                        return result;
                    })
                    .toList();

            registry.addRecipe(new JewelryTableEmiRecipe(
                    Identifier.of("gemstones", "/insert/" + idCounter),
                    JewelryTableEmiRecipeCategory.INSERT,
                    List.of(
                            EmiIngredient.of(List.of(EmiStack.of(ItemsRegistry.JEWELRY_HAMMER))),
                            EmiIngredient.of(bases.stream().map(EmiStack::of).toList()),
                            EmiIngredient.of(List.of(EmiStack.of(gem)))),
                    results.stream().map(EmiStack::of).toList(),
                    false));
            idCounter++;
        }

        for (ItemStack gem : JewelryTableRecipeExamples.allGemstoneStacks()) {
            registry.addRecipe(new JewelryTableEmiRecipe(
                    Identifier.of("gemstones", "/remove/" + idCounter),
                    JewelryTableEmiRecipeCategory.REMOVE,
                    List.of(
                            EmiIngredient.of(List.of(EmiStack.of(ItemsRegistry.JEWELRY_PLIERS))),
                            EmiIngredient.of(socketableItems.stream()
                                    .map(item -> JewelryTableRecipeExamples.baseWithSocketedGem(item, gem))
                                    .map(EmiStack::of)
                                    .toList())),
                    List.of(EmiStack.of(gem)),
                    true));
            idCounter++;
        }

        List<ItemStack> expandBases = socketableItems.stream()
                .map(JewelryTableRecipeExamples::baseWithLockedSocket)
                .toList();
        List<ItemStack> expandResults = socketableItems.stream()
                .map(JewelryTableRecipeExamples::baseWithExpandedSocket)
                .toList();

        registry.addRecipe(new JewelryTableEmiRecipe(
                Identifier.of("gemstones", "/expand/" + idCounter),
                JewelryTableEmiRecipeCategory.EXPAND,
                List.of(
                        EmiIngredient.of(List.of(
                                EmiStack.of(ItemsRegistry.DIAMOND_TIPPED_CHISEL),
                                EmiStack.of(ItemsRegistry.NETHERITE_TIPPED_CHISEL))),
                        EmiIngredient.of(expandBases.stream().map(EmiStack::of).toList()),
                        EmiIngredient.of(List.of(EmiStack.of(ItemsRegistry.EXPANSION_CRYSTAL)))),
                expandResults.stream().map(EmiStack::of).toList(),
                false));
    }
}
