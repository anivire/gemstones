package name.modid.core.content.events.loot;

import name.modid.Gemstones;
import name.modid.core.content.items.registries.GemstonesRegistry;
import name.modid.datagen.GemstoneLootHelper;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.Identifier;

public class BlocksLootTable {
  private static final int OBSIDIAN_DROP_WEIGHT = 800;
  private static final int OBSIDIAN_SHARD_CRUDE_WEIGHT = 120;
  private static final int OBSIDIAN_SHARD_POLISHED_WEIGHT = 50;
  private static final int OBSIDIAN_SHARD_FLAWLESS_WEIGHT = 20;
  private static final int OBSIDIAN_SHARD_RADIANT_WEIGHT = 10;

  public static LootTable replace(RegistryKey<LootTable> key, LootTable original, LootTableSource source,
      WrapperLookup registries) {
    if (!source.isBuiltin() || !key.getValue().getNamespace().equals("minecraft")) {
      return null;
    }

    String path = key.getValue().getPath();
    if (path.equals("blocks/obsidian")) {
      return obsidianShardReplacementTable(Items.OBSIDIAN).build();
    }
    if (path.equals("blocks/crying_obsidian")) {
      return obsidianShardReplacementTable(Items.CRYING_OBSIDIAN).build();
    }

    return null;
  }

  public static void setup(RegistryKey<LootTable> key, Builder tableBuilder, LootTableSource source,
      WrapperLookup registries) {
    if (!key.getValue().getNamespace().equals("minecraft")) {
      return;
    }

    String path = key.getValue().getPath();

    tableBuilder.pool(LootPool.builder()
        .with(LootTableEntry.builder(RegistryKey.of(
            RegistryKeys.LOOT_TABLE,
            Identifier.of(
                Gemstones.MOD_ID,
                "blocks/geode_from_ores")))));

    if (path.equals("blocks/gold_ore")
        || path.equals("blocks/deepslate_gold_ore")
        || path.equals("blocks/nether_gold_ore")) {
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getAmberGemstones(),
          0.1f));
    }

    if (path.equals("blocks/nether_quartz_ore")) {
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getZirconGemstones(),
          0.05f));
    }
  }

  private static Builder obsidianShardReplacementTable(Item originalBlock) {
    var gemstones = GemstonesRegistry.getObsidianShardGemstones();
    validateObsidianShardGemstones();

    return LootTable.builder()
        .pool(LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1))
            .conditionally(SurvivesExplosionLootCondition.builder())
            .with(ItemEntry.builder(originalBlock).weight(OBSIDIAN_DROP_WEIGHT))
            .with(ItemEntry.builder(gemstones.get(0)).weight(OBSIDIAN_SHARD_CRUDE_WEIGHT))
            .with(ItemEntry.builder(gemstones.get(1)).weight(OBSIDIAN_SHARD_POLISHED_WEIGHT))
            .with(ItemEntry.builder(gemstones.get(2)).weight(OBSIDIAN_SHARD_FLAWLESS_WEIGHT))
            .with(ItemEntry.builder(gemstones.get(3)).weight(OBSIDIAN_SHARD_RADIANT_WEIGHT)));
  }

  private static void validateObsidianShardGemstones() {
    var gemstones = GemstonesRegistry.getObsidianShardGemstones();
    if (gemstones.size() != 4) {
      throw new IllegalStateException(
          "Expected 4 obsidian shard gemstone qualities, got " + gemstones.size());
    }
  }
}
