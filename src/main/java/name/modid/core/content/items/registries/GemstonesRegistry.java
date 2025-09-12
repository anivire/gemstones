package name.modid.core.content.items.registries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.content.items.gemstones.AmberGemstoneItem;
import name.modid.core.content.items.gemstones.AquamarineGemstoneItem;
import name.modid.core.content.items.gemstones.CelestineGemstoneItem;
import name.modid.core.content.items.gemstones.GarnetGemstoneItem;
import name.modid.core.content.items.gemstones.JadeGemstoneItem;
import name.modid.core.content.items.gemstones.MalachiteGemstoneItem;
import name.modid.core.content.items.gemstones.ObsidianShardGemstoneItem;
import name.modid.core.content.items.gemstones.OpalGemstoneItem;
import name.modid.core.content.items.gemstones.PyriteGemstoneItem;
import name.modid.core.content.items.gemstones.RubyGemstoneItem;
import name.modid.core.content.items.gemstones.SapphireGemstoneItem;
import name.modid.core.content.items.gemstones.SpawnerCoreGemstoneItem;
import name.modid.core.content.items.gemstones.TopazGemstoneItem;
import name.modid.core.content.items.gemstones.ZirconGemstoneItem;
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

public class GemstonesRegistry {
  private static final List<Item> RUBY_GEMSTONES = new ArrayList<>();
  private static final List<Item> CELESTINE_GEMSTONES = new ArrayList<>();
  private static final List<Item> TOPAZ_GEMSTONES = new ArrayList<>();
  private static final List<Item> SAPPHIRE_GEMSTONES = new ArrayList<>();
  private static final List<Item> ZIRCON_GEMSTONES = new ArrayList<>();
  private static final List<Item> AQUAMARINE_GEMSTONES = new ArrayList<>();
  private static final List<Item> OBSIDIAN_SHARD_GEMSTONES = new ArrayList<>();
  private static final List<Item> OPAL_GEMSTONES = new ArrayList<>();
  private static final List<Item> JADE_GEMSTONES = new ArrayList<>();
  private static final List<Item> MALACHITE_GEMSTONES = new ArrayList<>();
  private static final List<Item> SPAWNER_CORE_GEMSTONES = new ArrayList<>();
  private static final List<Item> AMBER_GEMSTONES = new ArrayList<>();
  private static final List<Item> PYRITE_GEMSTONES = new ArrayList<>();
  private static final List<Item> GARNET_GEMSTONES = new ArrayList<>();

  public static final RegistryKey<ItemGroup> GEMSTONES_ITEM_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP,
      Identifier.of(Gemstones.MOD_ID, "item_group"));
  public static ItemGroup GEMSTONES_ITEM_GROUP;

  public static void registerItemGroup() {
    GEMSTONES_ITEM_GROUP = FabricItemGroup.builder()
        .icon(() -> new ItemStack(GemstonesRegistry.getRubyGemstones().get(0)))
        .displayName(Text.translatable("item_group.gemstones")).build();
    Registry.register(Registries.ITEM_GROUP, GEMSTONES_ITEM_GROUP_KEY, GEMSTONES_ITEM_GROUP);

    ItemGroupEvents.modifyEntriesEvent(GEMSTONES_ITEM_GROUP_KEY)
        .register(GemstonesRegistry::addGemstonesToItemGroup);
  }

  public static void register() {
    List<GemstoneQuality> rarities = Arrays.asList(GemstoneQuality.CRUDE, GemstoneQuality.POLISHED,
        GemstoneQuality.FLAWLESS, GemstoneQuality.RADIANT);
    for (GemstoneQuality rarity : rarities) {
      String rarityName = rarity.toString().toLowerCase();

      RUBY_GEMSTONES.add(ItemsRegistry.register("ruby_gemstone_" + rarityName,
          settings -> new RubyGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      CELESTINE_GEMSTONES.add(ItemsRegistry.register("celestine_gemstone_" + rarityName,
          settings -> new CelestineGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      TOPAZ_GEMSTONES.add(ItemsRegistry.register("topaz_gemstone_" + rarityName,
          settings -> new TopazGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      SAPPHIRE_GEMSTONES.add(ItemsRegistry.register("sapphire_gemstone_" + rarityName,
          settings -> new SapphireGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      ZIRCON_GEMSTONES.add(ItemsRegistry.register("zircon_gemstone_" + rarityName,
          settings -> new ZirconGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      AQUAMARINE_GEMSTONES.add(ItemsRegistry.register("aquamarine_gemstone_" + rarityName,
          settings -> new AquamarineGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      OBSIDIAN_SHARD_GEMSTONES.add(ItemsRegistry.register(
          "obsidian_shard_gemstone_" + rarityName,
          settings -> new ObsidianShardGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      OPAL_GEMSTONES.add(ItemsRegistry.register(
          "opal_gemstone_" + rarityName,
          settings -> new OpalGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      JADE_GEMSTONES.add(ItemsRegistry.register(
          "jade_gemstone_" + rarityName,
          settings -> new JadeGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      MALACHITE_GEMSTONES.add(ItemsRegistry.register(
          "malachite_gemstone_" + rarityName,
          settings -> new MalachiteGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      SPAWNER_CORE_GEMSTONES.add(ItemsRegistry.register(
          "spawner_core_gemstone_" + rarityName,
          settings -> new SpawnerCoreGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      AMBER_GEMSTONES.add(ItemsRegistry.register(
          "amber_gemstone_" + rarityName,
          settings -> new AmberGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      PYRITE_GEMSTONES.add(ItemsRegistry.register(
          "pyrite_gemstone_" + rarityName,
          settings -> new PyriteGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      GARNET_GEMSTONES.add(ItemsRegistry.register(
          "garnet_gemstone_" + rarityName,
          settings -> new GarnetGemstoneItem(settings, rarity),
          new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1)));
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
    MALACHITE_GEMSTONES.forEach(entries::add);
    SPAWNER_CORE_GEMSTONES.forEach(entries::add);
    AMBER_GEMSTONES.forEach(entries::add);
    PYRITE_GEMSTONES.forEach(entries::add);
    GARNET_GEMSTONES.forEach(entries::add);
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
    all.addAll(MALACHITE_GEMSTONES);
    all.addAll(SPAWNER_CORE_GEMSTONES);
    all.addAll(AMBER_GEMSTONES);
    all.addAll(PYRITE_GEMSTONES);
    all.addAll(GARNET_GEMSTONES);
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

  public static List<Item> getMalachiteGemstones() {
    return MALACHITE_GEMSTONES;
  }

  public static List<Item> getSpawnerCoreGemstones() {
    return SPAWNER_CORE_GEMSTONES;
  }

  public static List<Item> getAmberGemstones() {
    return AMBER_GEMSTONES;
  }

  public static List<Item> getPyriteGemstones() {
    return PYRITE_GEMSTONES;
  }

  public static List<Item> getGarnetGemstones() {
    return GARNET_GEMSTONES;
  }

  public static List<Item> getGemstonesByType(GemstoneType type) {
    return switch (type) {
      case RUBY -> RUBY_GEMSTONES;
      case CELESTINE -> CELESTINE_GEMSTONES;
      case TOPAZ -> TOPAZ_GEMSTONES;
      case SAPPHIRE -> SAPPHIRE_GEMSTONES;
      case ZIRCON -> ZIRCON_GEMSTONES;
      case AQUAMARINE -> AQUAMARINE_GEMSTONES;
      case OBSIDIAN_SHARD -> OBSIDIAN_SHARD_GEMSTONES;
      case OPAL -> OPAL_GEMSTONES;
      case JADE -> JADE_GEMSTONES;
      case MALACHITE -> MALACHITE_GEMSTONES;
      case SPAWNER_CORE -> SPAWNER_CORE_GEMSTONES;
      case AMBER -> AMBER_GEMSTONES;
      case PYRITE -> PYRITE_GEMSTONES;
      case GARNET -> GARNET_GEMSTONES;
      default -> List.of();
    };
  }
}
