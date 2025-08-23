package name.modid.helpers.modifiers.instance;

import name.modid.helpers.modifiers.type.ModifierItemCategory;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.text.MutableText;

public interface GemstoneModifier {
  public MutableText getTooltipString(GemstoneRarity gemstoneRarityType,
      Boolean withCategoryString);

  public GemstoneType getGemstoneType();

  public ModifierItemCategory getItemCategory();

  public GemstoneRarity getRarityType();

  public void setRarityType(GemstoneRarity type);
}
