package name.modid.core.api.modifiers.categories;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.impl.ModifierItemCategory;

public class Modifier extends AbstractModifier {
  private final ModifierConfig config;

  public Modifier(
      GemstoneType type,
      GemstoneQuality quality,
      ModifierItemCategory category,
      ModifierConfig config) {
    super(type, category, quality);
    this.config = config;
  }

  public ModifierConfig getConfig() {
    return config;
  }
}