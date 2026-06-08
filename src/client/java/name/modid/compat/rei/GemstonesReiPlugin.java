package name.modid.compat.rei;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import name.modid.Gemstones;
import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.components.GemstoneSlotsComponent;
import name.modid.core.api.entities.jeweleryTable.JewelryTableScreen;
import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.content.blocks.BlocksRegistry;
import name.modid.core.content.items.GemstoneItem;
import name.modid.core.content.items.registries.GemstonesRegistry;
import name.modid.core.content.items.registries.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GemstonesReiPlugin implements REIClientPlugin {
  public static final CategoryIdentifier<JewelryTableReiDisplay> INSERT = CategoryIdentifier
      .of(Identifier.of(Gemstones.MOD_ID, "jewelry_table_insert"));
  public static final CategoryIdentifier<JewelryTableReiDisplay> EXTRACT = CategoryIdentifier
      .of(Identifier.of(Gemstones.MOD_ID, "jewelry_table_extract"));
  public static final CategoryIdentifier<JewelryTableReiDisplay> EXPAND = CategoryIdentifier
      .of(Identifier.of(Gemstones.MOD_ID, "jewelry_table_expand"));

  @Override
  public void registerCategories(CategoryRegistry registry) {
    registry.add(new JewelryTableReiCategory(INSERT, Text.translatable("rei.gemstones.jewelry_table.insert")));
    registry.add(new JewelryTableReiCategory(EXTRACT, Text.translatable("rei.gemstones.jewelry_table.extract")));
    registry.add(new JewelryTableReiCategory(EXPAND, Text.translatable("rei.gemstones.jewelry_table.expand")));

    registry.addWorkstations(INSERT, EntryStacks.of(BlocksRegistry.JEWELRY_TABLE));
    registry.addWorkstations(EXTRACT, EntryStacks.of(BlocksRegistry.JEWELRY_TABLE));
    registry.addWorkstations(EXPAND, EntryStacks.of(BlocksRegistry.JEWELRY_TABLE));
  }

  @Override
  public void registerDisplays(DisplayRegistry registry) {
    insertDisplays().forEach(registry::add);
    extractDisplays().forEach(registry::add);
    expandDisplays().forEach(registry::add);
  }

  @Override
  public void registerScreens(ScreenRegistry registry) {
    registry.registerContainerClickArea(new Rectangle(92, 32, 28, 24), JewelryTableScreen.class,
        INSERT, EXTRACT, EXPAND);
  }

  private static List<JewelryTableReiDisplay> insertDisplays() {
    List<JewelryTableReiDisplay> displays = new ArrayList<>();
    List<Item> socketableItems = allSocketableItems();

    for (ItemStack gem : allGemstoneStacks()) {
      List<ItemStack> bases = socketableItems.stream()
          .map(GemstonesReiPlugin::baseWithEmptySocket)
          .toList();
      List<ItemStack> results = socketableItems.stream()
          .map(item -> {
            ItemStack result = baseWithEmptySocket(item);
            if (gem.getItem() instanceof GemstoneItem gemstone) {
              GemstoneSlotHelper.setGemstoneByIndex(result, 0, gemstone);
            }
            return result;
          })
          .toList();
      EntryIngredient baseIngredient = ingredient(bases);
      EntryIngredient resultIngredient = ingredient(results);
      EntryIngredient.unifyFocuses(baseIngredient, resultIngredient);

      displays.add(new JewelryTableReiDisplay(INSERT,
          List.of(
              ingredient(ItemsRegistry.JEWELRY_HAMMER),
              baseIngredient,
              ingredient(gem)),
          List.of(resultIngredient),
          false));
    }

    return displays;
  }

  private static List<JewelryTableReiDisplay> extractDisplays() {
    List<JewelryTableReiDisplay> displays = new ArrayList<>();
    List<Item> socketableItems = allSocketableItems();

    for (ItemStack gem : allGemstoneStacks()) {
      displays.add(new JewelryTableReiDisplay(EXTRACT,
          List.of(
              ingredient(ItemsRegistry.JEWELRY_PLIERS),
              ingredient(socketableItems.stream()
                  .map(item -> baseWithSocketedGem(item, gem))
                  .toList())),
          List.of(ingredient(gem)),
          true));
    }

    return displays;
  }

  private static List<JewelryTableReiDisplay> expandDisplays() {
    List<Item> socketableItems = allSocketableItems();
    List<ItemStack> bases = socketableItems.stream()
        .map(GemstonesReiPlugin::baseWithLockedSocket)
        .toList();
    List<ItemStack> results = socketableItems.stream()
        .map(item -> {
          ItemStack result = baseWithLockedSocket(item);
          GemstoneSlotHelper.addNewGemSlot(result);
          return result;
        })
        .toList();
    EntryIngredient baseIngredient = ingredient(bases);
    EntryIngredient resultIngredient = ingredient(results);
    EntryIngredient.unifyFocuses(baseIngredient, resultIngredient);

    return List.of(new JewelryTableReiDisplay(EXPAND,
        List.of(
            ingredient(List.of(
                new ItemStack(ItemsRegistry.DIAMOND_TIPPED_CHISEL),
                new ItemStack(ItemsRegistry.NETHERITE_TIPPED_CHISEL))),
            baseIngredient,
            ingredient(ItemsRegistry.EXPANSION_CRYSTAL)),
        List.of(resultIngredient),
        false));
  }

  private static List<Item> allSocketableItems() {
    return Registries.ITEM.stream()
        .filter(GemstoneSlotHelper::isItemValid)
        .sorted(Comparator.comparing(item -> Registries.ITEM.getId(item).toString()))
        .toList();
  }

  private static List<ItemStack> allGemstoneStacks() {
    return GemstonesRegistry.getAllGemstones().stream()
        .map(ItemStack::new)
        .toList();
  }

  private static ItemStack baseWithEmptySocket(Item item) {
    ItemStack stack = new ItemStack(item);
    stack.set(ComponentsRegistry.GEMSTONES,
        new GemstoneSlotsComponent(new GemstoneComponent[] {
            new GemstoneComponent(GemstoneType.EMPTY, GemstoneQuality.NONE),
            new GemstoneComponent(GemstoneType.LOCKED, GemstoneQuality.NONE)
        }));
    return stack;
  }

  private static ItemStack baseWithLockedSocket(Item item) {
    ItemStack stack = new ItemStack(item);
    stack.set(ComponentsRegistry.GEMSTONES,
        new GemstoneSlotsComponent(new GemstoneComponent[] {
            new GemstoneComponent(GemstoneType.EMPTY, GemstoneQuality.NONE),
            new GemstoneComponent(GemstoneType.LOCKED, GemstoneQuality.NONE)
        }));
    return stack;
  }

  private static ItemStack baseWithSocketedGem(Item item, ItemStack gem) {
    ItemStack stack = new ItemStack(item);
    if (gem.getItem() instanceof GemstoneItem gemstone) {
      stack.set(ComponentsRegistry.GEMSTONES,
          new GemstoneSlotsComponent(new GemstoneComponent[] {
              new GemstoneComponent(gemstone.getType(), gemstone.getRarityType()),
              new GemstoneComponent(GemstoneType.EMPTY, GemstoneQuality.NONE)
          }));
    }
    return stack;
  }

  private static EntryIngredient ingredient(Item item) {
    return ingredient(new ItemStack(item));
  }

  private static EntryIngredient ingredient(ItemStack stack) {
    return EntryIngredient.of(EntryStacks.of(stack));
  }

  private static EntryIngredient ingredient(List<ItemStack> stacks) {
    return EntryIngredient.of(stacks.stream()
        .map(EntryStacks::of)
        .toList());
  }
}
