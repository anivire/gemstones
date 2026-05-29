package name.modid.core.content.events.loot;

import name.modid.datagen.GemstoneLootHelper;
import name.modid.datapack.drops.DropsConfig;
import name.modid.datapack.drops.DropsRegistry;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.Identifier;

public class ChestsLootTable {
  public static void setup(RegistryKey<LootTable> key, Builder tableBuilder, LootTableSource source,
      WrapperLookup registries) {
    if (!key.getValue().getNamespace().equals("minecraft")) {
      return;
    }

    Identifier id = key.getValue();
    String fullPath = id.getNamespace() + ":" + id.getPath();

    for (DropsConfig.LootTableDrop drop : DropsRegistry.getStructuresLoot()) {
      for (String lootTable : drop.getLootTables()) {
        if (lootTable.equals(fullPath)) {
          for (DropsConfig.PoolEntry pool : drop.getPools()) {
            LootPool.Builder builder = GemstoneLootHelper.createPool(
                pool.getGemstoneType(), pool.getChance());
            if (builder != null) {
              tableBuilder.pool(builder);
            }
          }
        }
      }
    }
  }
}
