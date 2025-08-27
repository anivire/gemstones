package name.modid.helpers.modifiers.instance;

import java.util.Map;

import name.modid.config.datapack.ModifiersRegistry;
import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;

public class UniversalGemstoneModifierData implements ModifierData {
  private final GemstoneType gemstoneType;

  public UniversalGemstoneModifierData(GemstoneType gemstoneType) {
    this.gemstoneType = gemstoneType;
  }

  @Override
  public Map<ModifierItemCategory, Map<GemstoneRarity, GemstoneModifier>> getModifiers() {
    return ModifiersRegistry.getModifiersForGemstone(gemstoneType);
  }
}
