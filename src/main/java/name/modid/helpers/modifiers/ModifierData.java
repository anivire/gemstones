package name.modid.helpers.modifiers;

import java.util.HashMap;
import java.util.Map;

public interface ModifierData {
  Map<ModifierItemCaregory, GemstoneModifier> MODIFIERS = new HashMap<>();

  Map<ModifierItemCaregory, GemstoneModifier> getModifiers();
}
