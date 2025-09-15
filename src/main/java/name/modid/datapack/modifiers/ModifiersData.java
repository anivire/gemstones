package name.modid.datapack.modifiers;

import java.util.HashMap;
import java.util.Map;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.ModifierItemCategory;
import name.modid.core.api.modifiers.config.GemstoneModifier;

public interface ModifiersData {
  Map<ModifierItemCategory, GemstoneModifier> MODIFIERS = new HashMap<>();

  Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> getModifiers();
}
