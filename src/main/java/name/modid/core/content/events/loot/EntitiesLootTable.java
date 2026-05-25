package name.modid.core.content.events.loot;

import name.modid.core.content.items.registries.GemstonesRegistry;
import name.modid.datagen.GemstoneLootHelper;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class EntitiesLootTable {
  public static void setup(RegistryKey<LootTable> key, Builder tableBuilder, LootTableSource source,
      WrapperLookup registries) {
    if (!key.getValue().getNamespace().equals("minecraft")) {
      return;
    }

    String path = key.getValue().getPath();

    if (path.equals("entities/elder_guardian")) {
      tableBuilder.pool(GemstoneLootHelper.gemstonePool(
          GemstonesRegistry.getSapphireGemstones(),
          0.75f));
    }

    if (path.equals("entities/wither")) {
      tableBuilder.pool(GemstoneLootHelper.unusualGemstonePool(
          GemstonesRegistry
              .getWitherBoneGemstones()
              .get(0),
          1.0f));
    }

  }
}
