package name.modid.core.content.items.registries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import name.modid.Gemstones;
import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.content.items.GemstoneItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class GemstonesRegistry {
  private static final DeferredRegister<ItemGroup> ITEM_GROUPS = DeferredRegister.create(Gemstones.MOD_ID,
      RegistryKeys.ITEM_GROUP);

  private static final List<RegistrySupplier<Item>> RUBY_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> CELESTINE_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> TOPAZ_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> SAPPHIRE_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> ZIRCON_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> AQUAMARINE_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> OBSIDIAN_SHARD_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> OPAL_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> JADE_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> MALACHITE_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> RESTLESS_FLAME_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> AMBER_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> PYRITE_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> GARNET_GEMSTONES = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> WITHER_SHELL = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> POLYCHROME_CRYSTAL = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> ONYX = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> CRYSTALLIZED_EXPERIENCE = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> ASTRALITE = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> ENDER_SCALE = new ArrayList<>();
  private static final List<RegistrySupplier<Item>> LEGACY_GEMSTONES = new ArrayList<>();

  public static final RegistryKey<ItemGroup> GEMSTONES_ITEM_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP,
      Identifier.of(Gemstones.MOD_ID, "item_group"));
  public static RegistrySupplier<ItemGroup> GEMSTONES_ITEM_GROUP;

  public static void registerItemGroup() {
    GEMSTONES_ITEM_GROUP = ITEM_GROUPS.register("item_group", () -> CreativeTabRegistry.create(builder -> builder
        .displayName(Text.translatable("item_group.gemstones"))
        .icon(() -> new ItemStack(GemstonesRegistry.getRubyGemstones().get(0)))
        .entries((parameters, output) -> {
          for (Item item : ItemsRegistry.getAllItems()) {
            output.add(item);
          }
        })));

    ITEM_GROUPS.register();
  }

  public static void register() {
    List<GemstoneQuality> rarities = Arrays.asList(GemstoneQuality.CRUDE, GemstoneQuality.REFINED,
        GemstoneQuality.FLAWLESS, GemstoneQuality.RADIANT);

    // Default rarities
    for (GemstoneQuality rarity : rarities) {
      String rarityName = rarity.getPathName();

      RUBY_GEMSTONES.add(ItemsRegistry.register("ruby_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.RUBY, rarity),
          new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      CELESTINE_GEMSTONES.add(ItemsRegistry.register("celestine_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.CELESTINE, rarity),
          new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      TOPAZ_GEMSTONES.add(ItemsRegistry.register("topaz_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.TOPAZ, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      SAPPHIRE_GEMSTONES.add(ItemsRegistry.register("sapphire_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.SAPPHIRE, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      ZIRCON_GEMSTONES.add(ItemsRegistry.register("zircon_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.ZIRCON, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      AQUAMARINE_GEMSTONES.add(ItemsRegistry.register("aquamarine_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.AQUAMARINE, rarity),
          new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      OBSIDIAN_SHARD_GEMSTONES.add(ItemsRegistry.register(
          "obsidian_shard_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.OBSIDIAN_SHARD, rarity),
          new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      OPAL_GEMSTONES.add(ItemsRegistry.register(
          "opal_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.OPAL, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      JADE_GEMSTONES.add(ItemsRegistry.register(
          "jade_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.JADE, rarity),
          new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      MALACHITE_GEMSTONES.add(ItemsRegistry.register(
          "malachite_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.MALACHITE, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      RESTLESS_FLAME_GEMSTONES.add(ItemsRegistry.register(
          "restless_flame_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.RESTLESS_FLAME, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      AMBER_GEMSTONES.add(ItemsRegistry.register(
          "amber_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.AMBER, rarity),
          new Item.Settings().rarity(Rarity.UNCOMMON).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      PYRITE_GEMSTONES.add(ItemsRegistry.register(
          "pyrite_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.PYRITE, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      GARNET_GEMSTONES.add(ItemsRegistry.register(
          "garnet_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.GARNET, rarity),
          new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

      POLYCHROME_CRYSTAL.add(ItemsRegistry.register(
          "polychrome_crystal_gemstone_" + rarityName,
          settings -> new GemstoneItem(settings, GemstoneType.POLYCHROME_CRYSTAL, rarity),
          new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1)));
    }

    WITHER_SHELL.add(ItemsRegistry.register(
        "wither_shell_" + GemstoneQuality.MYTHIC.getPathName(),
        settings -> new GemstoneItem(settings, GemstoneType.WITHER_SHELL, GemstoneQuality.MYTHIC),
        () -> new Item.Settings().rarity(Rarity.EPIC)
            .component(DataComponentTypes.MAX_STACK_SIZE, 1)
            .component(ComponentsRegistry.explosionImmune(), true)));

    ONYX.add(ItemsRegistry.register(
        "onyx_" + GemstoneQuality.MYTHIC.getPathName(),
        settings -> new GemstoneItem(settings, GemstoneType.ONYX, GemstoneQuality.MYTHIC),
        new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

    CRYSTALLIZED_EXPERIENCE.add(ItemsRegistry.register(
        "crystallized_experience_" + GemstoneQuality.MYTHIC.getPathName(),
        settings -> new GemstoneItem(settings, GemstoneType.CRYSTALLIZED_EXPERIENCE, GemstoneQuality.MYTHIC),
        new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

    ASTRALITE.add(ItemsRegistry.register(
        "astralite_" + GemstoneQuality.MYTHIC.getPathName(),
        settings -> new GemstoneItem(settings, GemstoneType.ASTRALITE, GemstoneQuality.MYTHIC),
        new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

    ENDER_SCALE.add(ItemsRegistry.register(
        "ender_scale_" + GemstoneQuality.MYTHIC.getPathName(),
        settings -> new GemstoneItem(settings, GemstoneType.ENDER_SCALE, GemstoneQuality.MYTHIC),
        new Item.Settings().rarity(Rarity.EPIC).component(DataComponentTypes.MAX_STACK_SIZE, 1)));

    // Support for legacy gemstones, will be removed in next updates
    registerLegacyGemstone("ruby_gemstone_polished", GemstoneType.RUBY, GemstoneQuality.REFINED, Rarity.EPIC);
    registerLegacyGemstone("celestine_gemstone_polished", GemstoneType.CELESTINE, GemstoneQuality.REFINED,
        Rarity.UNCOMMON);
    registerLegacyGemstone("topaz_gemstone_polished", GemstoneType.TOPAZ, GemstoneQuality.REFINED, Rarity.RARE);
    registerLegacyGemstone("sapphire_gemstone_polished", GemstoneType.SAPPHIRE, GemstoneQuality.REFINED, Rarity.RARE);
    registerLegacyGemstone("zircon_gemstone_polished", GemstoneType.ZIRCON, GemstoneQuality.REFINED, Rarity.RARE);
    registerLegacyGemstone("aquamarine_gemstone_polished", GemstoneType.AQUAMARINE, GemstoneQuality.REFINED,
        Rarity.UNCOMMON);
    registerLegacyGemstone("obsidian_shard_gemstone_polished", GemstoneType.OBSIDIAN_SHARD, GemstoneQuality.REFINED,
        Rarity.UNCOMMON);
    registerLegacyGemstone("opal_gemstone_polished", GemstoneType.OPAL, GemstoneQuality.REFINED, Rarity.RARE);
    registerLegacyGemstone("jade_gemstone_polished", GemstoneType.JADE, GemstoneQuality.REFINED, Rarity.UNCOMMON);
    registerLegacyGemstone("malachite_gemstone_polished", GemstoneType.MALACHITE, GemstoneQuality.REFINED,
        Rarity.RARE);
    registerLegacyGemstone("restless_flame_polished", GemstoneType.RESTLESS_FLAME, GemstoneQuality.REFINED,
        Rarity.RARE);
    registerLegacyGemstone("amber_gemstone_polished", GemstoneType.AMBER, GemstoneQuality.REFINED, Rarity.UNCOMMON);
    registerLegacyGemstone("pyrite_gemstone_polished", GemstoneType.PYRITE, GemstoneQuality.REFINED, Rarity.RARE);
    registerLegacyGemstone("garnet_gemstone_polished", GemstoneType.GARNET, GemstoneQuality.REFINED, Rarity.RARE);
    registerLegacyGemstone("polychrome_crystal_gemstone_polished", GemstoneType.POLYCHROME_CRYSTAL,
        GemstoneQuality.REFINED, Rarity.EPIC);

    registerLegacyGemstone("wither_shell_unusual", GemstoneType.WITHER_SHELL, GemstoneQuality.MYTHIC, Rarity.EPIC);
    registerLegacyGemstone("onyx_unusual", GemstoneType.ONYX, GemstoneQuality.MYTHIC, Rarity.EPIC);
    registerLegacyGemstone("crystallized_experience_unusual", GemstoneType.CRYSTALLIZED_EXPERIENCE,
        GemstoneQuality.MYTHIC, Rarity.EPIC);
    registerLegacyGemstone("astralite_unusual", GemstoneType.ASTRALITE, GemstoneQuality.MYTHIC, Rarity.EPIC);
    registerLegacyGemstone("ender_scale_unusual", GemstoneType.ENDER_SCALE, GemstoneQuality.MYTHIC, Rarity.EPIC);
  }

  private static void registerLegacyGemstone(String path, GemstoneType type, GemstoneQuality quality, Rarity rarity) {
    LEGACY_GEMSTONES.add(ItemsRegistry.register(path,
        settings -> new GemstoneItem(settings, type, quality),
        new Item.Settings().rarity(rarity).component(DataComponentTypes.MAX_STACK_SIZE, 1)));
  }

  public static void addGemstonesToItemGroup(java.util.function.Consumer<Item> entries) {
    getAllGemstones().forEach(entries);
  }

  public static List<Item> getAllGemstones() {
    List<Item> all = new ArrayList<>();
    all.addAll(get(RUBY_GEMSTONES));
    all.addAll(get(CELESTINE_GEMSTONES));
    all.addAll(get(TOPAZ_GEMSTONES));
    all.addAll(get(SAPPHIRE_GEMSTONES));
    all.addAll(get(ZIRCON_GEMSTONES));
    all.addAll(get(AQUAMARINE_GEMSTONES));
    all.addAll(get(OBSIDIAN_SHARD_GEMSTONES));
    all.addAll(get(OPAL_GEMSTONES));
    all.addAll(get(JADE_GEMSTONES));
    all.addAll(get(MALACHITE_GEMSTONES));
    all.addAll(get(RESTLESS_FLAME_GEMSTONES));
    all.addAll(get(AMBER_GEMSTONES));
    all.addAll(get(PYRITE_GEMSTONES));
    all.addAll(get(GARNET_GEMSTONES));
    all.addAll(get(WITHER_SHELL));
    all.addAll(get(POLYCHROME_CRYSTAL));
    all.addAll(get(ONYX));
    all.addAll(get(CRYSTALLIZED_EXPERIENCE));
    all.addAll(get(ASTRALITE));
    all.addAll(get(ENDER_SCALE));
    return all;
  }

  public static List<Item> getLegacyGemstones() {
    return get(LEGACY_GEMSTONES);
  }

  public static List<Item> getRubyGemstones() {
    return get(RUBY_GEMSTONES);
  }

  public static List<Item> getCelestineGemstones() {
    return get(CELESTINE_GEMSTONES);
  }

  public static List<Item> getSapphireGemstones() {
    return get(SAPPHIRE_GEMSTONES);
  }

  public static List<Item> getTopazGemstones() {
    return get(TOPAZ_GEMSTONES);
  }

  public static List<Item> getZirconGemstones() {
    return get(ZIRCON_GEMSTONES);
  }

  public static List<Item> getAquamarineGemstones() {
    return get(AQUAMARINE_GEMSTONES);
  }

  public static List<Item> getObsidianShardGemstones() {
    return get(OBSIDIAN_SHARD_GEMSTONES);
  }

  public static List<Item> getOpalGemstones() {
    return get(OPAL_GEMSTONES);
  }

  public static List<Item> getJadeGemstones() {
    return get(JADE_GEMSTONES);
  }

  public static List<Item> getMalachiteGemstones() {
    return get(MALACHITE_GEMSTONES);
  }

  public static List<Item> getSpawnerCoreGemstones() {
    return get(RESTLESS_FLAME_GEMSTONES);
  }

  public static List<Item> getAmberGemstones() {
    return get(AMBER_GEMSTONES);
  }

  public static List<Item> getPyriteGemstones() {
    return get(PYRITE_GEMSTONES);
  }

  public static List<Item> getGarnetGemstones() {
    return get(GARNET_GEMSTONES);
  }

  public static List<Item> getWitherBoneGemstones() {
    return get(WITHER_SHELL);
  }

  public static List<Item> getPolychromeCrystalGemstones() {
    return get(POLYCHROME_CRYSTAL);
  }

  public static List<Item> getOnyxGemstones() {
    return get(ONYX);
  }

  public static List<Item> getCrystallizedExperienceGemstones() {
    return get(CRYSTALLIZED_EXPERIENCE);
  }

  public static List<Item> getAstraliteGemstones() {
    return get(ASTRALITE);
  }

  public static List<Item> getEnderScaleGemstones() {
    return get(ENDER_SCALE);
  }

  public static List<Item> getGemstonesByType(GemstoneType type) {
    return switch (type) {
      case RUBY -> get(RUBY_GEMSTONES);
      case CELESTINE -> get(CELESTINE_GEMSTONES);
      case TOPAZ -> get(TOPAZ_GEMSTONES);
      case SAPPHIRE -> get(SAPPHIRE_GEMSTONES);
      case ZIRCON -> get(ZIRCON_GEMSTONES);
      case AQUAMARINE -> get(AQUAMARINE_GEMSTONES);
      case OBSIDIAN_SHARD -> get(OBSIDIAN_SHARD_GEMSTONES);
      case OPAL -> get(OPAL_GEMSTONES);
      case JADE -> get(JADE_GEMSTONES);
      case MALACHITE -> get(MALACHITE_GEMSTONES);
      case RESTLESS_FLAME -> get(RESTLESS_FLAME_GEMSTONES);
      case AMBER -> get(AMBER_GEMSTONES);
      case PYRITE -> get(PYRITE_GEMSTONES);
      case GARNET -> get(GARNET_GEMSTONES);
      case WITHER_SHELL -> get(WITHER_SHELL);
      case POLYCHROME_CRYSTAL -> get(POLYCHROME_CRYSTAL);
      case ONYX -> get(ONYX);
      case CRYSTALLIZED_EXPERIENCE -> get(CRYSTALLIZED_EXPERIENCE);
      case ASTRALITE -> get(ASTRALITE);
      case ENDER_SCALE -> get(ENDER_SCALE);
      default -> List.of();
    };
  }

  private static List<Item> get(List<RegistrySupplier<Item>> suppliers) {
    return suppliers.stream().map(RegistrySupplier::get).toList();
  }
}
