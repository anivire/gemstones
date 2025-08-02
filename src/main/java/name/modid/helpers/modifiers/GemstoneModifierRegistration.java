package name.modid.helpers.modifiers;

import java.util.HashMap;
import java.util.Map;

import name.modid.helpers.modifiers.data.AquamarinModifierData;
import name.modid.helpers.modifiers.data.CelestineModifierData;
import name.modid.helpers.modifiers.data.RubyModifierData;
import name.modid.helpers.modifiers.data.SapphireModifierData;
import name.modid.helpers.modifiers.data.TopazModifierData;
import name.modid.helpers.modifiers.data.ZirconModifierData;
import name.modid.helpers.types.GemstoneType;

public class GemstoneModifierRegistration {
  private static final Map<GemstoneType, GemstonesModifierData> MODIFIER_REGISTRY = new HashMap<>();

  static {
    MODIFIER_REGISTRY.put(GemstoneType.RUBY, new RubyModifierData());
    MODIFIER_REGISTRY.put(GemstoneType.CELESTINE, new CelestineModifierData());
    MODIFIER_REGISTRY.put(GemstoneType.TOPAZ, new TopazModifierData());
    MODIFIER_REGISTRY.put(GemstoneType.SAPPHIRE, new SapphireModifierData());
    MODIFIER_REGISTRY.put(GemstoneType.ZIRCON, new ZirconModifierData());
    MODIFIER_REGISTRY.put(GemstoneType.AQUAMARIN, new AquamarinModifierData());
  }

  public static Map<GemstoneType, GemstonesModifierData> MODIFIER_REGISTRY() { return MODIFIER_REGISTRY; }
}
