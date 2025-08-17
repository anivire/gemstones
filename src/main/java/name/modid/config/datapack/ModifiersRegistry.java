package name.modid.config.datapack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierItemCaregory;
import name.modid.helpers.types.GemstoneType;

public class ModifiersRegistry {
  private static final Map<GemstoneType, Map<ModifierItemCaregory, GemstoneModifier>> CACHED_MODIFIERS = new ConcurrentHashMap<>();

  public static Map<ModifierItemCaregory, GemstoneModifier> getModifiersForGemstone(GemstoneType type) {
    return CACHED_MODIFIERS.computeIfAbsent(type, ModifiersDataFactory::createModifiers);
  }

  public static void clearCache() {
    CACHED_MODIFIERS.clear();
  }
}
