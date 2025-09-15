package name.modid.core.api.modifiers.categories;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.impl.GemstoneModifier;
import name.modid.core.api.modifiers.impl.ModifierItemCategory;
import name.modid.core.api.modifiers.tooltips.TooltipBuilder;
import net.minecraft.text.MutableText;

public abstract class AbstractModifier implements GemstoneModifier {
  protected final GemstoneType gemstoneType;
  protected final ModifierItemCategory itemCategory;
  protected final GemstoneQuality rarityType;

  protected AbstractModifier(GemstoneType gemstoneType, ModifierItemCategory itemCategory, GemstoneQuality rarityType) {
    this.gemstoneType = gemstoneType;
    this.itemCategory = itemCategory;
    this.rarityType = rarityType;
  }

  @Override
  public GemstoneType getGemstoneType() {
    return gemstoneType;
  }

  @Override
  public GemstoneQuality getRarityType() {
    return rarityType;
  }

  @Override
  public ModifierItemCategory getItemCategory() {
    return itemCategory;
  }

  @Override
  public MutableText getTooltipText(GemstoneQuality GemstoneQualityType,
      Boolean isItemTooltip) {
    return new TooltipBuilder(
        this.gemstoneType,
        this.itemCategory,
        GemstoneQualityType,
        this)
        .withItemTooltip(isItemTooltip)
        .build();
  }
}
