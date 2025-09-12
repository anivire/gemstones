package name.modid.core.content.items.gemstones;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;

public class SpawnerCoreGemstoneItem extends GemstoneItem {
  public SpawnerCoreGemstoneItem(Settings settings, GemstoneQuality rarityType) {
    super(settings, GemstoneType.SPAWNER_CORE, rarityType);
  }
}
