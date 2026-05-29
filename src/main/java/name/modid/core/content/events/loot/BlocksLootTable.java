package name.modid.core.content.events.loot;

import java.util.Map;

import name.modid.Gemstones;
import name.modid.datagen.GemstoneLootHelper;
import name.modid.datapack.drops.DropsConfig;
import name.modid.datapack.drops.DropsRegistry;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.Identifier;

public class BlocksLootTable {
  public static void setup(RegistryKey<LootTable> key, Builder tableBuilder, LootTableSource source,
      WrapperLookup registries) {
    if (!isMinecraftBlockLootTable(key)) {
      return;
    }

    tableBuilder.pool(LootPool.builder()
        .with(LootTableEntry.builder(RegistryKey.of(
            RegistryKeys.LOOT_TABLE,
            Identifier.of(
                Gemstones.MOD_ID,
                "blocks/geode_from_ores")))));

    Identifier id = key.getValue();
    String fullPath = id.getNamespace() + ":" + id.getPath();

    Map<String, DropsConfig.BlockDropEntry> entryMap = DropsRegistry.getBlockDrops();
    DropsConfig.BlockDropEntry entry = entryMap.get(fullPath);
    if (entry == null) return;

    for (DropsConfig.DropEntry drop : entry.getEntries()) {
      if (drop.getChance() <= 0) continue;

      if (drop.getGemstoneType() != null) {
        addGemstoneDrop(tableBuilder, drop.getGemstoneType(), drop.getChance());
      } else if (drop.getItem() != null) {
        Item item = Registries.ITEM.get(drop.getItem());
        if (item != Items.AIR) {
          tableBuilder.pool(LootPool.builder()
              .rolls(ConstantLootNumberProvider.create(1))
              .with(ItemEntry.builder(item))
              .conditionally(RandomChanceLootCondition.builder(drop.getChance()))
              .conditionally(SurvivesExplosionLootCondition.builder()));
        }
      }
    }
  }

  private static void addGemstoneDrop(Builder table, name.modid.core.api.modifiers.types.GemstoneType type, float chance) {
    LootPool.Builder pool = GemstoneLootHelper.createPool(type, chance);
    if (pool == null) return;
    pool.conditionally(SurvivesExplosionLootCondition.builder());
    table.pool(pool);
  }

  private static boolean isMinecraftBlockLootTable(RegistryKey<LootTable> key) {
    Identifier id = key.getValue();
    return id.getNamespace().equals("minecraft") && id.getPath().startsWith("blocks/");
  }
}
