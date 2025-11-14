package name.modid.datapack.modifiers;

import java.util.HashMap;
import java.util.Map;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;

public class ModifiersDataFactory {
  public static Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> createModifiers(
      GemstoneType gemstoneType) {

    Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> modifiers = new HashMap<>();
    ModifiersRawConfig config = ModifiersDataLoader.getLoadedConfigs().get(gemstoneType);

    if (config == null || config.modifiers == null)
      return modifiers;

    config.modifiers.forEach((category, cfg) -> {
      Map<GemstoneQuality, GemstoneModifier> rarityMap = new HashMap<>();
      for (GemstoneQuality quality : GemstoneQuality.values()) {
        if (quality == GemstoneQuality.NONE)
          continue;

        rarityMap.put(quality, new GemstoneModifier(gemstoneType, quality, category, cfg));
      }

      if (!rarityMap.isEmpty()) {
        modifiers.put(category, rarityMap);
      }
    });

    return modifiers;
  }
}