package name.modid.datagen;

import java.util.List;
import java.util.Map;

import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.content.items.registries.GemstonesRegistry;
import name.modid.datapack.drops.DropsRegistry;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

public final class GemstoneLootHelper {
  private GemstoneLootHelper() {
  }

  public static LootPool.Builder createPool(GemstoneType type, float chance) {
    List<Item> gemstones = GemstonesRegistry.getGemstonesByType(type);
    if (gemstones.isEmpty()) return null;

    LootPool.Builder pool = LootPool.builder()
        .rolls(ConstantLootNumberProvider.create(1))
        .conditionally(RandomChanceLootCondition.builder(chance));

    if (gemstones.size() == 1) {
      pool.with(ItemEntry.builder(gemstones.get(0)));
    } else {
      Map<GemstoneQuality, Float> quality = DropsRegistry.getQualityDistribution();
      pool.with(ItemEntry.builder(gemstones.get(0))
          .weight(Math.max(1, Math.round(quality.getOrDefault(GemstoneQuality.CRUDE, 0f) * 100))));
      pool.with(ItemEntry.builder(gemstones.get(1))
          .weight(Math.max(1, Math.round(quality.getOrDefault(GemstoneQuality.REFINED, 0f) * 100))));
      pool.with(ItemEntry.builder(gemstones.get(2))
          .weight(Math.max(1, Math.round(quality.getOrDefault(GemstoneQuality.FLAWLESS, 0f) * 100))));
      pool.with(ItemEntry.builder(gemstones.get(3))
          .weight(Math.max(1, Math.round(quality.getOrDefault(GemstoneQuality.RADIANT, 0f) * 100))));
    }
    return pool;
  }
}
