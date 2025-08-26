package name.modid.helpers.modifiers.instance;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.tooltips.GemstoneTooltipBuilder;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.text.MutableText;

public abstract class AbstractModifier implements GemstoneModifier {
  protected final GemstoneType gemstoneType;
  protected final ModifierItemCategory itemCategory;
  protected final GemstoneRarity rarityType;

  protected AbstractModifier(GemstoneType gemstoneType, ModifierItemCategory itemCategory, GemstoneRarity rarityType) {
    this.gemstoneType = gemstoneType;
    this.itemCategory = itemCategory;
    this.rarityType = rarityType;
  }

  @Override
  public GemstoneType getGemstoneType() {
    return gemstoneType;
  }

  @Override
  public GemstoneRarity getRarityType() {
    return rarityType;
  }

  @Override
  public ModifierItemCategory getItemCategory() {
    return itemCategory;
  }

  @Override
  public MutableText getTooltipText(GemstoneRarity gemstoneRarityType,
      Boolean isItemTooltip) {
    return new GemstoneTooltipBuilder(
        this.gemstoneType,
        this.itemCategory,
        gemstoneRarityType,
        this)
        .withItemTooltip(isItemTooltip)
        .build();
  }
}
