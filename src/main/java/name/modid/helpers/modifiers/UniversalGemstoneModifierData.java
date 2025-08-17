package name.modid.helpers.modifiers;

import java.util.Map;

import name.modid.config.datapack.ModifiersRegistry;
import name.modid.helpers.types.GemstoneType;

public class UniversalGemstoneModifierData implements ModifierData {
  private final GemstoneType gemstoneType;

  public UniversalGemstoneModifierData(GemstoneType gemstoneType) {
    this.gemstoneType = gemstoneType;
  }

  @Override
  public Map<ModifierItemCaregory, GemstoneModifier> getModifiers() {
    return ModifiersRegistry.getModifiersForGemstone(gemstoneType);
  }
}
