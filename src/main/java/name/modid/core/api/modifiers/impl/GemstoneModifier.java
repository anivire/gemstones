package name.modid.core.api.modifiers.impl;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import net.minecraft.text.MutableText;

public interface GemstoneModifier {
  public GemstoneType getGemstoneType();

  public GemstoneQuality getRarityType();

  public ModifierItemCategory getItemCategory();

  public MutableText getTooltipText(GemstoneQuality GemstoneQualityType,
      Boolean withCategoryString);
}
