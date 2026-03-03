package name.modid.datagen;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

public final class GemstoneLootHelper {
  private GemstoneLootHelper() {
  }

  private static final float CRUDE = 0.60f;
  private static final float POLISHED = 0.25f;
  private static final float FLAWLESS = 0.10f;
  private static final float RADIANT = 0.05f;

  public static LootPool.Builder gemstonePool(
      List<Item> gemstones,
      float dropChance) {
    if (gemstones.size() != 4) {
      throw new IllegalStateException(
          "Expected 4 gemstone default qualities, got " + gemstones.size());
    }

    return LootPool.builder()
        .rolls(ConstantLootNumberProvider.create(1))
        .conditionally(RandomChanceLootCondition.builder(dropChance))
        .with(
            ItemEntry.builder(gemstones.get(0))
                .conditionally(RandomChanceLootCondition.builder(CRUDE)))
        .with(
            ItemEntry.builder(gemstones.get(1))
                .conditionally(RandomChanceLootCondition.builder(POLISHED)))
        .with(
            ItemEntry.builder(gemstones.get(2))
                .conditionally(RandomChanceLootCondition.builder(FLAWLESS)))
        .with(
            ItemEntry.builder(gemstones.get(3))
                .conditionally(RandomChanceLootCondition.builder(RADIANT)));
  }

  public static LootPool.Builder unusualGemstonePool(
      Item item,
      float dropChance) {
    return LootPool.builder()
        .rolls(ConstantLootNumberProvider.create(1))
        .conditionally(RandomChanceLootCondition.builder(dropChance))
        .with(ItemEntry.builder(item));
  }
}