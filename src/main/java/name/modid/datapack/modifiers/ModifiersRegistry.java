package name.modid.datapack.modifiers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.ModifierItemCategory;
import name.modid.core.api.modifiers.config.GemstoneModifier;

public class ModifiersRegistry {
  private static final Map<GemstoneType, Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>>> CACHED_MODIFIERS = new ConcurrentHashMap<>();
  private static final Map<GemstoneType, ModifiersData> MODIFIER_REGISTRY = new HashMap<>();

  static {
    for (GemstoneType type : GemstoneType.values()) {
      MODIFIER_REGISTRY.put(type, new UniversalGemstoneModifierData(type));
    }
  }

  public static Map<GemstoneType, ModifiersData> MODIFIER_REGISTRY() {
    return MODIFIER_REGISTRY;
  }

  public static Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> getModifiersForGemstone(
      GemstoneType type) {
    return CACHED_MODIFIERS.computeIfAbsent(type, ModifiersDataFactory::createModifiers);
  }

  public static void clearCache() {
    CACHED_MODIFIERS.clear();
  }
}
