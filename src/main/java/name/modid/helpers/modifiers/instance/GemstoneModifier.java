package name.modid.helpers.modifiers.instance;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.text.MutableText;

public interface GemstoneModifier {
  public GemstoneType getGemstoneType();

  public GemstoneRarity getRarityType();

  public ModifierItemCategory getItemCategory();

  public MutableText getTooltipText(GemstoneRarity gemstoneRarityType,
      Boolean withCategoryString);
}
