package name.modid.core.utils.geode;

import java.util.Map;
import java.util.Random;

import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;

public final class GeodeQualitySelector {
  private GeodeQualitySelector() {
  }

  public static Map<GemstoneQuality, Float> eligibleQualities(
      GemstoneType gemstoneType,
      Map<GemstoneQuality, Float> configuredQualities,
      Map<GemstoneType, Map<GemstoneQuality, Float>> qualityOverrides) {
    if (qualityOverrides == null) {
      return configuredQualities;
    }

    Map<GemstoneQuality, Float> overrideQualities = qualityOverrides.get(gemstoneType);
    return overrideQualities == null || overrideQualities.isEmpty()
        ? configuredQualities
        : overrideQualities;
  }

  public static GemstoneQuality select(
      GemstoneType gemstoneType,
      Map<GemstoneQuality, Float> configuredQualities,
      Map<GemstoneType, Map<GemstoneQuality, Float>> qualityOverrides,
      Random random) {
    Map<GemstoneQuality, Float> qualities = eligibleQualities(gemstoneType, configuredQualities, qualityOverrides);
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
}
