package name.modid.core.api.modifiers.config;

import java.util.ArrayList;

public interface ModifierHandler<T extends ModifierConfig> {
  default boolean supports(GemstoneModifier modifier) {
    return true;
  }

  void apply(ArrayList<GemstoneModifier> modifiers, ModifierContext ctx);
}
