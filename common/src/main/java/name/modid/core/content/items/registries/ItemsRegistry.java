package name.modid.core.content.items.registries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import name.modid.Gemstones;
import name.modid.core.content.blocks.BlocksRegistry;
import name.modid.core.content.items.ExpansionCrystalItem;
import name.modid.core.content.items.GeodeItem;
import name.modid.core.content.items.MossyBox;
import name.modid.core.content.items.tools.ChiselItem;
import name.modid.core.content.items.tools.JewelryFileItem;
import name.modid.core.content.items.tools.JewelryHammerItem;
import name.modid.core.content.items.tools.JewelryPliersItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;

public final class ItemsRegistry {
  private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Gemstones.MOD_ID, RegistryKeys.ITEM);

  public static final RegistrySupplier<Item> DEEPSLATE_GEODE = register("deepslate_geode",
      settings -> new GeodeItem(settings, "deepslate_geode"),
      new Item.Settings().rarity(Rarity.EPIC).maxCount(16));

  public static final RegistrySupplier<Item> STONE_GEODE = register("stone_geode",
      settings -> new GeodeItem(settings, "stone_geode"),
      new Item.Settings().rarity(Rarity.UNCOMMON).maxCount(16));

  public static final RegistrySupplier<Item> MOSSY_BOX = register("mossy_box",
      settings -> new MossyBox(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 16));

  public static final RegistrySupplier<Item> DIAMOND_TIPPED_CHISEL = register("diamond_tipped_chisel",
      settings -> new ChiselItem(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1).maxDamage(15));

  public static final RegistrySupplier<Item> NETHERITE_TIPPED_CHISEL = register("netherite_tipped_chisel",
      settings -> new ChiselItem(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1).maxDamage(55));

  public static final RegistrySupplier<Item> JEWELRY_PLIERS = register("jewelry_pliers",
      settings -> new JewelryPliersItem(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1).maxDamage(75));

  public static final RegistrySupplier<Item> JEWELRY_FILE = register("jewelry_file",
      settings -> new JewelryFileItem(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1).maxDamage(55));

  public static final RegistrySupplier<Item> JEWELRY_HAMMER = register("jewelry_hammer",
      settings -> new JewelryHammerItem(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1).maxDamage(75));

  public static final RegistrySupplier<Item> EXPANSION_CRYSTAL = register("expansion_crystal",
      settings -> new ExpansionCrystalItem(settings),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 16));

  public static RegistrySupplier<Item> register(String path, Function<Item.Settings, Item> factory,
      Item.Settings settings) {
    return register(path, factory, () -> settings);
  }

  public static RegistrySupplier<Item> register(String path, Function<Item.Settings, Item> factory,
      Supplier<Item.Settings> settings) {
    return ITEMS.register(path, () -> factory.apply(settings.get()));
  }

  public static List<Item> getAllItems() {
    List<Item> all = new ArrayList<>();
    all.addAll(GemstonesRegistry.getAllGemstones());
    // all.addAll(GemstonesRegistry.getLegacyGemstones());
    all.add(stoneGeode());
    all.add(deepslateGeode());
    all.add(mossyBox());
    all.add(diamondTippedChisel());
    all.add(netheriteTippedChisel());
    all.add(jewelryFile());
    all.add(jewelryPliers());
    all.add(jewelryHammer());
    all.add(expansionCrystal());
    all.add(BlocksRegistry.jewelryTable().asItem());
    return all;
  }

  public static void initialize() {
    Gemstones.LOGGER.info("Registering items for {}", Gemstones.MOD_ID);

    GemstonesRegistry.register();
    ITEMS.register();
    GemstonesRegistry.registerItemGroup();
  }

  public static Item deepslateGeode() {
    return DEEPSLATE_GEODE.get();
  }

  public static Item stoneGeode() {
    return STONE_GEODE.get();
  }

  public static Item mossyBox() {
    return MOSSY_BOX.get();
  }

  public static Item diamondTippedChisel() {
    return DIAMOND_TIPPED_CHISEL.get();
  }

  public static Item netheriteTippedChisel() {
    return NETHERITE_TIPPED_CHISEL.get();
  }

  public static Item jewelryPliers() {
    return JEWELRY_PLIERS.get();
  }

  public static Item jewelryFile() {
    return JEWELRY_FILE.get();
  }

  public static Item jewelryHammer() {
    return JEWELRY_HAMMER.get();
  }

  public static Item expansionCrystal() {
    return EXPANSION_CRYSTAL.get();
  }
}
