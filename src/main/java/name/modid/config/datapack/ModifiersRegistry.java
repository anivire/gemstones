package name.modid.config.datapack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.type.ModifierItemCategory;

public class ModifiersRegistry {
  private static final Map<GemstoneType, Map<ModifierItemCategory, GemstoneModifier>> CACHED_MODIFIERS = new ConcurrentHashMap<>();

  public static Map<ModifierItemCategory, GemstoneModifier> getModifiersForGemstone(GemstoneType type) {
    return CACHED_MODIFIERS.computeIfAbsent(type, ModifiersDataFactory::createModifiers);
  }

  public static void clearCache() {
    CACHED_MODIFIERS.clear();
  }
}
