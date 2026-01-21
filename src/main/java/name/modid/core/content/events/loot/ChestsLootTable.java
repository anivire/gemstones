package name.modid.core.content.events.loot;

import name.modid.core.content.items.registries.GemstonesRegistry;
import name.modid.datagen.GemstoneLootHelper;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class ChestsLootTable {
  public static void setup(RegistryKey<LootTable> key, Builder tableBuilder, LootTableSource source,
      WrapperLookup registries) {
    if (!key.getValue().getNamespace().equals("minecraft")) {
      return;
    }

    String path = key.getValue().getPath();

    if (path.equals("chests/jungle_temple")) {
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getJadeGemstones(),
          0.3f));
    }

    if (path.equals("chests/bastion_treasure")) {
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getRubyGemstones(),
          0.35f));
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getPyriteGemstones(),
          0.45f));
    }

    if (path.equals("chests/desert_pyramid")) {
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getTopazGemstones(),
          0.4f));
    }

    if (path.equals("chests/end_city_treasure")) {
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getPolychromeCrystalGemstones(),
          0.35f));
      tableBuilder.pool(GemstoneLootHelper.unusualGemstonePool(
          GemstonesRegistry.getOnyxGemstones().get(0),
          0.2f));
      tableBuilder.pool(GemstoneLootHelper.unusualGemstonePool(
          GemstonesRegistry.getAstraliteGemstones().get(0),
          0.15f));
    }

    if (path.equals("chests/stronghold_library")) {
      tableBuilder.pool(GemstoneLootHelper.unusualGemstonePool(
          GemstonesRegistry
              .getCrystallizedExpirienceGemstones()
              .get(0),
          0.1f));
    }

    if (path.equals("chests/trial_chambers/reward")) {
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getSpawnerCoreGemstones(),
          0.4f));
    }

    if (path.equals("chests/trial_chambers/supply")) {
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getSpawnerCoreGemstones(),
          0.2f));
    }

    if (path.equals("chests/trial_chambers/entrance")) {
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getSpawnerCoreGemstones(),
          0.15f));
    }

    if (path.equals("chests/simple_dungeon")) {
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getSpawnerCoreGemstones(),
          0.1f));
    }
  }
}
