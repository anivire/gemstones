package name.modid.core.content.items.registries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import name.modid.Gemstones;
import name.modid.core.content.blocks.BlocksRegistry;
import name.modid.core.content.items.ExpansionCrystalItem;
import name.modid.core.content.items.GeodeItem;
import name.modid.core.content.items.MossyBox;
import name.modid.core.content.items.tools.ChiselItem;
import name.modid.core.content.items.tools.JewelryHammerItem;
import name.modid.core.content.items.tools.JewelryPliersItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class ItemsRegistry {
  public static final Item DEEPSLATE_GEODE = Registry.register(
      Registries.ITEM,
      Identifier.of(Gemstones.MOD_ID, "deepslate_geode"),
      new GeodeItem(new Item.Settings().rarity(Rarity.EPIC).maxCount(16), "deepslate_geode"));

  public static final Item STONE_GEODE = Registry.register(
      Registries.ITEM,
      Identifier.of(Gemstones.MOD_ID, "stone_geode"),
      new GeodeItem(new Item.Settings().rarity(Rarity.UNCOMMON).maxCount(16), "stone_geode"));

  public static final Item MOSSY_BOX = register("mossy_box",
      settings -> new MossyBox(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 16));

  public static final Item DIAMOND_TIPPED_CHISEL = register("diamond_tipped_chisel",
      settings -> new ChiselItem(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1).maxDamage(15));

  public static final Item NETHERITE_TIPPED_CHISEL = register("netherite_tipped_chisel",
      settings -> new ChiselItem(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1).maxDamage(55));

  public static final Item JEWELRY_PLIERS = register("jewelry_pliers",
      settings -> new JewelryPliersItem(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1).maxDamage(75));

  public static final Item JEWELRY_HAMMER = register("jewelry_hammer",
      settings -> new JewelryHammerItem(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1).maxDamage(75));

  public static final Item EXPANSION_CRYSTAL = register("expansion_crystal",
      settings -> new ExpansionCrystalItem(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 8));

  public static Item register(String path, Function<Item.Settings, Item> factory,
      Item.Settings settings) {
    final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Gemstones.MOD_ID, path));
    return Registry.register(Registries.ITEM, registryKey, factory.apply(settings));
  }

  public static List<Item> getAllItems() {
    List<Item> all = new ArrayList<>();
    all.addAll(GemstonesRegistry.getAllGemstones());
    all.add(STONE_GEODE);
    all.add(DEEPSLATE_GEODE);
    all.add(MOSSY_BOX);
    all.add(DIAMOND_TIPPED_CHISEL);
    all.add(NETHERITE_TIPPED_CHISEL);
    all.add(JEWELRY_PLIERS);
    all.add(JEWELRY_HAMMER);
    all.add(EXPANSION_CRYSTAL);
    all.add(BlocksRegistry.JEWELRY_TABLE.asItem());
    return all;
  }

  public static void initialize() {
    Gemstones.LOGGER.info("Registering items for {}", Gemstones.MOD_ID);

    GemstonesRegistry.register();
    GemstonesRegistry.registerItemGroup();

    ItemGroupEvents.modifyEntriesEvent(GemstonesRegistry.GEMSTONES_ITEM_GROUP_KEY)
        .register(entries -> {
          entries.add(STONE_GEODE);
          entries.add(DEEPSLATE_GEODE);
          entries.add(MOSSY_BOX);
          entries.add(DIAMOND_TIPPED_CHISEL);
          entries.add(NETHERITE_TIPPED_CHISEL);
          entries.add(JEWELRY_PLIERS);
          entries.add(JEWELRY_HAMMER);
          entries.add(EXPANSION_CRYSTAL);
          entries.add(BlocksRegistry.JEWELRY_TABLE.asItem());
        });
  }
}
