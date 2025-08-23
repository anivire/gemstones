package name.modid.helpers.modifiers.instance;

import java.util.HashMap;
import java.util.Map;

import name.modid.helpers.modifiers.type.ModifierItemCategory;

public interface ModifierData {
  Map<ModifierItemCategory, GemstoneModifier> MODIFIERS = new HashMap<>();

  Map<ModifierItemCategory, GemstoneModifier> getModifiers();
}
