package name.modid.helpers;

import name.modid.Gemstones;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;
import name.modid.items.geodes.GeodeDeepslateItem;
import name.modid.items.geodes.GeodeStoneItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

public final class ItemRegistrationHelper {
  public static final Item STONE_GEODE = register(
      "stone_geode",
      settings -> new GeodeStoneItem(settings,
          new ArrayList<>(
              Arrays.asList(GemstoneRarity.COMMON, GemstoneRarity.UNCOMMON, GemstoneRarity.RARE)),
          new ArrayList<>(Arrays.asList(GemstoneType.RUBY, GemstoneType.CELESTINE))),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 64));

  public static final Item DEEPSLATE_GEODE = register("deepslate_geode",
      settings -> new GeodeDeepslateItem(settings,
          new ArrayList<>(Arrays.asList(GemstoneRarity.UNCOMMON, GemstoneRarity.RARE,
              GemstoneRarity.LEGENDARY)),
          new ArrayList<>(Arrays.asList(GemstoneType.RUBY, GemstoneType.CELESTINE))),
      new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 64));

  public static Item register(String path, Function<Item.Settings, Item> factory,
      Item.Settings settings) {
    final RegistryKey<Item> registryKey =
        RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Gemstones.MOD_ID, path));
    return Registry.register(Registries.ITEM, registryKey, factory.apply(settings)); // Removed
                                                                                     // registryKey
                                                                                     // call
  }

  public static void initialize() {
    Gemstones.LOGGER.info("Registering items for {}", Gemstones.MOD_ID);

    GemstonesRegistrationHelper.register();
    GemstonesRegistrationHelper.registerItemGroup();

    ItemGroupEvents.modifyEntriesEvent(GemstonesRegistrationHelper.GEMSTONES_ITEM_GROUP_KEY)
        .register(entries -> {
          entries.add(STONE_GEODE);
          entries.add(DEEPSLATE_GEODE);
        });
  }
}
