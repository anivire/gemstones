package name.modid.helpers.modifiers;

import java.util.HashMap;
import java.util.Map;

import name.modid.helpers.types.GemstoneType;

public class ModifierRegistration {
  private static final Map<GemstoneType, ModifierData> MODIFIER_REGISTRY = new HashMap<>();

  static {
    for (GemstoneType type : GemstoneType.values()) {
      MODIFIER_REGISTRY.put(type, new UniversalGemstoneModifierData(type));
    }
  }

  public static Map<GemstoneType, ModifierData> MODIFIER_REGISTRY() {
    return MODIFIER_REGISTRY;
  }
}
