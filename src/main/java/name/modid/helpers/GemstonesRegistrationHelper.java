package name.modid.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import name.modid.Gemstones;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.items.gemstones.AquamarineGemstoneItem;
import name.modid.items.gemstones.CelestineGemstoneItem;
import name.modid.items.gemstones.JadeGemstoneItem;
import name.modid.items.gemstones.ObsidianShardGemstoneItem;
import name.modid.items.gemstones.OpalGemstoneItem;
import name.modid.items.gemstones.RubyGemstoneItem;
import name.modid.items.gemstones.SapphireGemstoneItem;
import name.modid.items.gemstones.TopazGemstoneItem;
import name.modid.items.gemstones.ZirconGemstoneItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class GemstonesRegistrationHelper {
  private static final List<Item> RUBY_GEMSTONES = new ArrayList<>();
  private static final List<Item> CELESTINE_GEMSTONES = new ArrayList<>();
  private static final List<Item> TOPAZ_GEMSTONES = new ArrayList<>();
  private static final List<Item> SAPPHIRE_GEMSTONES = new ArrayList<>();
  private static final List<Item> ZIRCON_GEMSTONES = new ArrayList<>();
  private static final List<Item> AQUAMARINE_GEMSTONES = new ArrayList<>();
  private static final List<Item> OBSIDIAN_SHARD_GEMSTONES = new ArrayList<>();
  private static final List<Item> OPAL_GEMSTONES = new ArrayList<>();
  private static final List<Item> JADE_GEMSTONES = new ArrayList<>();

  public static final RegistryKey<ItemGroup> GEMSTONES_ITEM_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP,
      Identifier.of(Gemstones.MOD_ID, "item_group"));
  public static ItemGroup GEMSTONES_ITEM_GROUP;

  public static void registerItemGroup() {
    GEMSTONES_ITEM_GROUP = FabricItemGroup.builder()
        .icon(() -> new ItemStack(GemstonesRegistrationHelper.getRubyGemstones().get(0)))
        .displayName(Text.translatable("item_group.gemstones")).build();
    Registry.register(Registries.ITEM_GROUP, GEMSTONES_ITEM_GROUP_KEY, GEMSTONES_ITEM_GROUP);

    ItemGroupEvents.modifyEntriesEvent(GEMSTONES_ITEM_GROUP_KEY)
        .register(GemstonesRegistrationHelper::addGemstonesToItemGroup);
  }

  public static void register() {
    List<GemstoneRarity> rarities = Arrays.asList(GemstoneRarity.COMMON, GemstoneRarity.UNCOMMON,
        GemstoneRarity.RARE, GemstoneRarity.LEGENDARY);
    for (GemstoneRarity rarity : rarities) {
      String rarityName = rarity.toString().toLowerCase();

      // RUBY
      Item rubyGemstone = ItemRegistrationHelper.register("ruby_gemstone_" + rarityName,
          settings -> new RubyGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1));
      RUBY_GEMSTONES.add(rubyGemstone);

      // CELESTINE
      Item celestineGemstone = ItemRegistrationHelper.register("celestine_gemstone_" + rarityName,
          settings -> new CelestineGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1));
      CELESTINE_GEMSTONES.add(celestineGemstone);

      // TOPAZ
      Item topazGemstone = ItemRegistrationHelper.register("topaz_gemstone_" + rarityName,
          settings -> new TopazGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1));
      TOPAZ_GEMSTONES.add(topazGemstone);

      // CELESTINE
      Item sapphireGemstone = ItemRegistrationHelper.register("sapphire_gemstone_" + rarityName,
          settings -> new SapphireGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1));
      SAPPHIRE_GEMSTONES.add(sapphireGemstone);

      // ZIRCON
      Item zirconGemstone = ItemRegistrationHelper.register("zircon_gemstone_" + rarityName,
          settings -> new ZirconGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1));
      ZIRCON_GEMSTONES.add(zirconGemstone);

      // AQUAMARINE
      Item aquamarineGemstone = ItemRegistrationHelper.register("aquamarine_gemstone_" + rarityName,
          settings -> new AquamarineGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1));
      AQUAMARINE_GEMSTONES.add(aquamarineGemstone);

      // AQUAMARINE
      Item obsidianShardGemstone = ItemRegistrationHelper.register(
          "obsidian_shard_gemstone_" + rarityName,
          settings -> new ObsidianShardGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1));
      OBSIDIAN_SHARD_GEMSTONES.add(obsidianShardGemstone);

      // OPAL
      Item opalGemstone = ItemRegistrationHelper.register(
          "opal_gemstone_" + rarityName,
          settings -> new OpalGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1));
      OPAL_GEMSTONES.add(opalGemstone);

      // JADE
      Item jadeGemstone = ItemRegistrationHelper.register(
          "jade_gemstone_" + rarityName,
          settings -> new JadeGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1));
      JADE_GEMSTONES.add(jadeGemstone);
    }
  }

  public static void addGemstonesToItemGroup(FabricItemGroupEntries entries) {
    RUBY_GEMSTONES.forEach(entries::add);
    CELESTINE_GEMSTONES.forEach(entries::add);
    TOPAZ_GEMSTONES.forEach(entries::add);
    SAPPHIRE_GEMSTONES.forEach(entries::add);
    ZIRCON_GEMSTONES.forEach(entries::add);
    AQUAMARINE_GEMSTONES.forEach(entries::add);
    OBSIDIAN_SHARD_GEMSTONES.forEach(entries::add);
    OPAL_GEMSTONES.forEach(entries::add);
    JADE_GEMSTONES.forEach(entries::add);
  }

  public static List<Item> getAllGemstones() {
    List<Item> all = new ArrayList<>();
    all.addAll(RUBY_GEMSTONES);
    all.addAll(CELESTINE_GEMSTONES);
    all.addAll(TOPAZ_GEMSTONES);
    all.addAll(SAPPHIRE_GEMSTONES);
    all.addAll(ZIRCON_GEMSTONES);
    all.addAll(AQUAMARINE_GEMSTONES);
    all.addAll(OBSIDIAN_SHARD_GEMSTONES);
    all.addAll(OPAL_GEMSTONES);
    all.addAll(JADE_GEMSTONES);
    return all;
  }

  public static List<Item> getRubyGemstones() {
    return RUBY_GEMSTONES;
  }

  public static List<Item> getCelestineGemstones() {
    return CELESTINE_GEMSTONES;
  }

  public static List<Item> getSapphireGemstones() {
    return SAPPHIRE_GEMSTONES;
  }

  public static List<Item> getTopazGemstones() {
    return TOPAZ_GEMSTONES;
  }

  public static List<Item> getZirconGemstones() {
    return ZIRCON_GEMSTONES;
  }

  public static List<Item> getAquamarineGemstones() {
    return AQUAMARINE_GEMSTONES;
  }

  public static List<Item> getObsidianShardGemstones() {
    return OBSIDIAN_SHARD_GEMSTONES;
  }

  public static List<Item> getOpalGemstones() {
    return OPAL_GEMSTONES;
  }

  public static List<Item> getJadeGemstones() {
    return JADE_GEMSTONES;
  }
}
