package name.modid.items.geodes;

import java.util.ArrayList;

import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;

public class GeodeDeepslateItem extends GeodeItem {
  public GeodeDeepslateItem(Settings settings, ArrayList<GemstoneRarity> gemstoneRarities,
      ArrayList<GemstoneType> includedGemstones) {
    super(settings, new ArrayList<>(gemstoneRarities), new ArrayList<>(includedGemstones));
  }
}
