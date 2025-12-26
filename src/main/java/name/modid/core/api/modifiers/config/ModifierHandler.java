package name.modid.core.api.modifiers.config;

import java.util.ArrayList;

public interface ModifierHandler<T extends ModifierConfig> {
  void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx);
}