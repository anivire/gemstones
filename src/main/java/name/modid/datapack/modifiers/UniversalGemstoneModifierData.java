package name.modid.datapack.modifiers;

import java.util.Map;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.ModifierItemCategory;
import name.modid.core.api.modifiers.impl.GemstoneModifier;

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
