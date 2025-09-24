package name.modid.datapack.modifiers;

import java.util.Map;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;

public class ModifiersRawConfig {
  public final GemstoneType gemstone_type;
  public final Map<ModifierItemCategory, ModifierConfig> modifiers;

  public ModifiersRawConfig(GemstoneType gemstoneType, Map<ModifierItemCategory, ModifierConfig> modifiers) {
    this.gemstone_type = gemstoneType;
    this.modifiers = modifiers;
  }
}