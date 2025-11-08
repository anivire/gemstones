package name.modid.datagen.providers;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import name.modid.Gemstones;
import name.modid.core.content.blocks.BlocksRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public class OverallLootTableProvider extends SimpleFabricLootTableProvider {
  public OverallLootTableProvider(FabricDataOutput output,
      CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
    super(output, registriesFuture, LootContextTypes.BLOCK);
  }

  @Override
  public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> exporter) {
    LootTable.Builder jewelryTableLoot = LootTable.builder()
        .pool(
            LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0f))
                .with(ItemEntry.builder(BlocksRegistry.JEWELRY_TABLE))
                .conditionally(SurvivesExplosionLootCondition.builder()));

    exporter.accept(blockLootKey("jewelry_table"), jewelryTableLoot);
  }

  private static RegistryKey<LootTable> blockLootKey(String path) {
    Identifier id = Identifier.of(Gemstones.MOD_ID, "blocks/" + path);
    return RegistryKey.of(RegistryKeys.LOOT_TABLE, id);
  }
}
