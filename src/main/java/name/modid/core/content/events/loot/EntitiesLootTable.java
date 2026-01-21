package name.modid.core.content.events.loot;

import name.modid.core.content.items.registries.GemstonesRegistry;
import name.modid.datagen.GemstoneLootHelper;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
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
          0.35f));
    }

    if (path.equals("entities/wither")) {
      tableBuilder.pool(GemstoneLootHelper.unusualGemstonePool(
          GemstonesRegistry
              .getWitherBoneGemstones()
              .get(0),
          1.0f));
    }

    if (path.equals("entities/ender_dragon")) {
      tableBuilder.pool(
          LootPool.builder()
              .rolls(ConstantLootNumberProvider.create(1))
              .with(ItemEntry.builder(GemstonesRegistry.getEnderScaleGemstones().get(0)).apply(
                  SetCountLootFunction.builder(
                      UniformLootNumberProvider.create(1, 4)))));
    }
  }
}
