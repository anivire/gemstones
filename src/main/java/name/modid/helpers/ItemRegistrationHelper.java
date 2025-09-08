package name.modid.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import name.modid.Gemstones;
import name.modid.items.MossyBox;
import name.modid.items.geodes.GeodeItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class ItemRegistrationHelper {
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
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 64));

  public static Item register(String path, Function<Item.Settings, Item> factory,
      Item.Settings settings) {
    final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Gemstones.MOD_ID, path));
    return Registry.register(Registries.ITEM, registryKey, factory.apply(settings));
  }

  public static List<Item> getAllItems() {
    List<Item> all = new ArrayList<>();
    all.addAll(GemstonesRegistrationHelper.getAllGemstones());
    all.add(STONE_GEODE);
    all.add(DEEPSLATE_GEODE);
    all.add(MOSSY_BOX);
    return all;
  }

  public static void initialize() {
    Gemstones.LOGGER.info("Registering items for {}", Gemstones.MOD_ID);

    GemstonesRegistrationHelper.register();
    GemstonesRegistrationHelper.registerItemGroup();

    ItemGroupEvents.modifyEntriesEvent(GemstonesRegistrationHelper.GEMSTONES_ITEM_GROUP_KEY)
        .register(entries -> {
          entries.add(STONE_GEODE);
          entries.add(DEEPSLATE_GEODE);
          entries.add(MOSSY_BOX);
        });
  }
}
