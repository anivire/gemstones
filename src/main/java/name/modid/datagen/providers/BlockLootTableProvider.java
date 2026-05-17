package name.modid.datagen.providers;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import name.modid.Gemstones;
import name.modid.core.content.blocks.BlocksRegistry;
import name.modid.core.content.items.registries.ItemsRegistry;
import name.modid.core.content.loot.BlockInTagLootCondition;
import name.modid.core.content.loot.GeodeDropChanceCondition;
import name.modid.core.content.registries.TagsRegistry;
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

public class BlockLootTableProvider extends SimpleFabricLootTableProvider {
  private final float BASE_STONE_CHANCE = 0.03f;
  private final float BASE_DEEPSLATE_CHANCE = 0.02f;

  public BlockLootTableProvider(FabricDataOutput output,
      CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
    super(output, registriesFuture, LootContextTypes.BLOCK);
  }

  @Override
  public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> exporter) {
    exporter.accept(loot("jewelry_table"),
        LootTable.builder()
            .pool(LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0f))
                .with(ItemEntry.builder(BlocksRegistry.JEWELRY_TABLE))
                .conditionally(SurvivesExplosionLootCondition.builder())));

    exporter.accept(
        loot("geode_from_ores"),
        LootTable.builder()
            .pool(
                LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(SurvivesExplosionLootCondition.builder())
                    .conditionally(
                        BlockInTagLootCondition.builder(
                            TagsRegistry.STONE_ORES))
                    .conditionally(
                        GeodeDropChanceCondition.builder(BASE_STONE_CHANCE))
                    .with(ItemEntry.builder(ItemsRegistry.STONE_GEODE)))
            .pool(
                LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(SurvivesExplosionLootCondition.builder())
                    .conditionally(
                        BlockInTagLootCondition.builder(
                            TagsRegistry.DEEPSLATE_ORES))
                    .conditionally(
                        GeodeDropChanceCondition.builder(BASE_DEEPSLATE_CHANCE))
                    .with(ItemEntry.builder(ItemsRegistry.DEEPSLATE_GEODE))));
  }

  private RegistryKey<LootTable> loot(String path) {
    Identifier id = Identifier.of(Gemstones.MOD_ID, "blocks/" + path);
    return RegistryKey.of(RegistryKeys.LOOT_TABLE, id);
  }
}
