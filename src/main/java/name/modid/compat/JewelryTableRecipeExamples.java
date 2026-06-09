package name.modid.compat;

import java.util.Comparator;
import java.util.List;

import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.components.GemstoneSlotsComponent;
import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.content.items.GemstoneItem;
import name.modid.core.content.items.registries.GemstonesRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public final class JewelryTableRecipeExamples {

    private JewelryTableRecipeExamples() {
    }

    public static List<Item> allSocketableItems() {
        return Registries.ITEM.stream()
                .filter(GemstoneSlotHelper::isItemValid)
                .sorted(Comparator.comparing(item -> Registries.ITEM.getId(item).toString()))
                .toList();
    }

    public static List<ItemStack> allGemstoneStacks() {
        return GemstonesRegistry.getAllGemstones().stream()
                .map(ItemStack::new)
                .toList();
    }

    public static ItemStack baseWithEmptySocket(Item item) {
        ItemStack stack = new ItemStack(item);
        stack.set(ComponentsRegistry.GEMSTONES,
                new GemstoneSlotsComponent(new GemstoneComponent[]{
                        new GemstoneComponent(GemstoneType.EMPTY, GemstoneQuality.NONE),
                        new GemstoneComponent(GemstoneType.LOCKED, GemstoneQuality.NONE)
                }));
        return stack;
    }

    public static ItemStack baseWithLockedSocket(Item item) {
        ItemStack stack = new ItemStack(item);
        stack.set(ComponentsRegistry.GEMSTONES,
                new GemstoneSlotsComponent(new GemstoneComponent[]{
                        new GemstoneComponent(GemstoneType.EMPTY, GemstoneQuality.NONE),
                        new GemstoneComponent(GemstoneType.LOCKED, GemstoneQuality.NONE)
                }));
        return stack;
    }

    public static ItemStack baseWithSocketedGem(Item item, ItemStack gem) {
        ItemStack stack = new ItemStack(item);
        if (gem.getItem() instanceof GemstoneItem gemstone) {
            stack.set(ComponentsRegistry.GEMSTONES,
                    new GemstoneSlotsComponent(new GemstoneComponent[]{
                            new GemstoneComponent(gemstone.getType(), gemstone.getRarityType()),
                            new GemstoneComponent(GemstoneType.EMPTY, GemstoneQuality.NONE)
                    }));
        }
        return stack;
    }

    public static ItemStack baseWithExpandedSocket(Item item) {
        ItemStack stack = baseWithLockedSocket(item);
        GemstoneSlotHelper.addNewGemSlot(stack);
        return stack;
    }
}
