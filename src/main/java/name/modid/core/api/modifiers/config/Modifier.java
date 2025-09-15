package name.modid.core.api.modifiers.config;

import name.modid.core.api.modifiers.tooltips.TooltipBuilder;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import net.minecraft.text.MutableText;

public class Modifier implements GemstoneModifier {
  private final GemstoneType gemstoneType;
  private final ModifierItemCategory itemCategory;
  private final GemstoneQuality rarityType;
  private final ModifierConfig config;

  public Modifier(
      GemstoneType type,
      GemstoneQuality quality,
      ModifierItemCategory category,
      ModifierConfig config) {
    this.gemstoneType = type;
    this.itemCategory = category;
    this.rarityType = quality;
    this.config = config;
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
  public MutableText getTooltipText(GemstoneQuality quality, Boolean isItemTooltip) {
    return new TooltipBuilder(gemstoneType, itemCategory, quality, this)
        .withItemTooltip(isItemTooltip)
        .build();
  }

  public ModifierConfig getConfig() {
    return config;
  }
}