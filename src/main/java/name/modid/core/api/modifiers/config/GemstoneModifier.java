package name.modid.core.api.modifiers.config;

import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import net.minecraft.text.MutableText;

public interface GemstoneModifier {
  public GemstoneType getGemstoneType();

  public GemstoneQuality getRarityType();

  public ModifierItemCategory getItemCategory();

  public MutableText getTooltipText(GemstoneQuality GemstoneQualityType,
      Boolean withCategoryString);
}
