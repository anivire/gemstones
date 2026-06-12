package name.modid.datapack.modifiers;

import java.util.Map;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;

public class UniversalGemstoneModifierData implements ModifiersData {
  private final GemstoneType gemstoneType;

  public UniversalGemstoneModifierData(GemstoneType gemstoneType) {
    this.gemstoneType = gemstoneType;
  }

  @Override
  public Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> getModifiers() {
    return ModifiersRegistry.getModifiersForGemstone(gemstoneType);
  }
}
