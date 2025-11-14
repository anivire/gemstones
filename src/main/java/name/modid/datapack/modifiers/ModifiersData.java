package name.modid.datapack.modifiers;

import java.util.HashMap;
import java.util.Map;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.ModifierItemCategory;

public interface ModifiersData {
  Map<ModifierItemCategory, GemstoneModifier> MODIFIERS = new HashMap<>();

  Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> getModifiers();
}
