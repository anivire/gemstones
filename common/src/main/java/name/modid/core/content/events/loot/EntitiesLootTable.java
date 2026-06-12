package name.modid.core.content.events.loot;

import dev.architectury.event.events.common.LootEvent;
import name.modid.datagen.GemstoneLootHelper;
import name.modid.datapack.drops.DropsConfig;
import name.modid.datapack.drops.DropsRegistry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class EntitiesLootTable {
  public static void setup(RegistryKey<LootTable> key, LootEvent.LootTableModificationContext context,
      boolean builtin) {
    if (!key.getValue().getNamespace().equals("minecraft")) {
      return;
    }

    Identifier id = key.getValue();
    String fullPath = id.getNamespace() + ":" + id.getPath();

    for (DropsConfig.LootTableDrop drop : DropsRegistry.getEntitiesLoot()) {
      for (String lootTable : drop.getLootTables()) {
        if (lootTable.equals(fullPath)) {
          for (DropsConfig.PoolEntry pool : drop.getPools()) {
            LootPool.Builder builder = GemstoneLootHelper.createPool(
                pool.getGemstoneType(), pool.getChance());
            if (builder != null) {
              context.addPool(builder);
            }
          }
        }
      }
    }
  }
}
