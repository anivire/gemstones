package name.modid.datagen.providers;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import name.modid.Gemstones;
import name.modid.core.content.items.registries.GemstonesRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public class ChestLootTableProvider
    extends SimpleFabricLootTableProvider {

  public ChestLootTableProvider(
      FabricDataOutput output,
      CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
    super(output, registriesFuture, LootContextTypes.CHEST);
  }

  @Override
  public void accept(
      BiConsumer<RegistryKey<LootTable>, LootTable.Builder> exporter) {
    exporter.accept(
        RegistryKey.of(
            RegistryKeys.LOOT_TABLE,
            Identifier.of(Gemstones.MOD_ID, "mossy_box_loot")),
        LootTable.builder()
            .pool(LootPool.builder()
                .rolls(UniformLootNumberProvider.create(1.0f, 2.0f))
                .with(ItemEntry.builder(Items.CARROT).weight(5)).apply(SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(1.0f, 3.0f)))
                .with(ItemEntry.builder(Items.DRIED_KELP).weight(5).apply(SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(2.0f, 4.0f))))
                .with(ItemEntry.builder(Items.COD).weight(3).apply(SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(2.0f, 3.0f))))
                .with(ItemEntry.builder(Items.SALMON).weight(3).apply(SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(2.0f, 3.0f))))
                .with(ItemEntry.builder(Items.TROPICAL_FISH).weight(2).apply(SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(1.0f, 2.0f)))))
            .pool(LootPool.builder()
                .rolls(UniformLootNumberProvider.create(1.0f, 2.0f))
                .with(ItemEntry.builder(Items.COAL).weight(4).apply(SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(2.0f, 6.0f))))
                .with(ItemEntry.builder(Items.IRON_NUGGET).weight(4).apply(SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(2.0f, 5.0f))))
                .with(ItemEntry.builder(Items.IRON_INGOT).weight(4).apply(SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(1.0f, 2.0f))))
                .with(ItemEntry.builder(Items.STRING).weight(4).apply(SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(1.0f, 2.0f))))
                .with(ItemEntry.builder(Items.KELP).weight(4).apply(SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(2.0f, 5.0f))))
                .with(ItemEntry.builder(Items.FEATHER).weight(2).apply(SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(1.0f, 2.0f))))
                .with(ItemEntry.builder(Items.FISHING_ROD)
                    .weight(2)
                    .apply(SetDamageLootFunction.builder(ConstantLootNumberProvider.create(0.8f)))))
            .pool(LootPool.builder()
                .rolls(UniformLootNumberProvider.create(0.0f, 1.0f))
                .with(ItemEntry.builder(GemstonesRegistry.getCelestineGemstones().get(0)).weight(1))
                .with(ItemEntry.builder(GemstonesRegistry.getCelestineGemstones().get(1)).weight(1))
                .with(ItemEntry.builder(GemstonesRegistry.getAquamarineGemstones().get(0)).weight(1))
                .with(ItemEntry.builder(GemstonesRegistry.getAquamarineGemstones().get(1)).weight(1))
                .with(ItemEntry.builder(GemstonesRegistry.getZirconGemstones().get(0)).weight(1))
                .with(ItemEntry.builder(GemstonesRegistry.getZirconGemstones().get(1)).weight(1))
                .with(ItemEntry.builder(Items.NAUTILUS_SHELL).weight(1))
                .with(ItemEntry.builder(Items.EMERALD).weight(1).apply(SetCountLootFunction.builder(
                    UniformLootNumberProvider.create(1.0f, 2.0f))))));
  }
}