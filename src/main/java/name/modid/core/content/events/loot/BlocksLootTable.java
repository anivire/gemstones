package name.modid.core.content.events.loot;

import name.modid.Gemstones;
import name.modid.core.content.items.registries.GemstonesRegistry;
import name.modid.datagen.GemstoneLootHelper;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.Identifier;

public class BlocksLootTable {
  private static final float DEFAULT_GEMSTONE_DROP_CHANCE = 0.085f;

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

    if (path.equals("blocks/obsidian")
        || path.equals("blocks/crying_obsidian")) {
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getObsidianShardGemstones(),
          0.2f));
    }

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
}
