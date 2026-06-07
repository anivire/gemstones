package name.modid.core.utils.geode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;

public final class GeodeQualitySelector {
  private GeodeQualitySelector() {
  }

  public static Map<GemstoneQuality, Float> eligibleQualities(
      GemstoneType gemstoneType,
      Map<GemstoneQuality, Float> configuredQualities) {
    if (gemstoneType != GemstoneType.POLYCHROME_CRYSTAL) {
      return configuredQualities;
    }

    Map<GemstoneQuality, Float> eligible = new LinkedHashMap<>();

    // drop only two qualities
    putIfPresent(eligible, configuredQualities, GemstoneQuality.CRUDE);
    putIfPresent(eligible, configuredQualities, GemstoneQuality.POLISHED);

    return eligible.isEmpty() ? configuredQualities : eligible;
  }

  public static GemstoneQuality select(
      GemstoneType gemstoneType,
      Map<GemstoneQuality, Float> configuredQualities,
      Random random) {
    Map<GemstoneQuality, Float> qualities = eligibleQualities(gemstoneType, configuredQualities);
    float totalRarityChance = qualities.values().stream().reduce(0f, Float::sum);
    float randRarity = random.nextFloat() * totalRarityChance;

    GemstoneQuality selectedRarity = null;
    float cumulativeRarity = 0f;

    for (var entry : qualities.entrySet()) {
      cumulativeRarity += entry.getValue();
      if (randRarity <= cumulativeRarity) {
        selectedRarity = entry.getKey();
        break;
      }
    }

    if (selectedRarity == null) {
      selectedRarity = qualities.keySet().iterator().next();
    }

    return selectedRarity;
  }

  private static void putIfPresent(
      Map<GemstoneQuality, Float> target,
      Map<GemstoneQuality, Float> source,
      GemstoneQuality quality) {
    Float weight = source.get(quality);
    if (weight != null) {
      target.put(quality, weight);
    }
  }
}
