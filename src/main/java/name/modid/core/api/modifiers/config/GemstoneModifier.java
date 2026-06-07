package name.modid.core.api.modifiers.config;

import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.core.api.tooltips.TooltipBuilder;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.Nullable;

public class GemstoneModifier {
  private final GemstoneType gemstoneType;
  private final ModifierItemCategory itemCategory;
  private final GemstoneQuality rarityType;
  private final ModifierConfig config;

  public GemstoneModifier(
      GemstoneType type,
      GemstoneQuality quality,
      ModifierItemCategory category,
      ModifierConfig config) {
    this.gemstoneType = type;
    this.itemCategory = category;
    this.rarityType = quality;
    this.config = config;
  }

  public GemstoneType getGemstoneType() {
    return gemstoneType;
  }

  public GemstoneQuality getRarityType() {
    return rarityType;
  }

  public ModifierItemCategory getItemCategory() {
    return itemCategory;
  }

  public ModifierConfig getConfig() {
    return config;
  }

  public MutableText getTooltipText(GemstoneQuality quality, Boolean isItemTooltip) {
    return new TooltipBuilder(gemstoneType, itemCategory, quality, this)
        .withItemTooltip(isItemTooltip)
        .build();
  }

  public MutableText getTooltipText(
      GemstoneQuality quality,
      Boolean isItemTooltip,
      @Nullable GemstoneModifier baseModifier) {
    return new TooltipBuilder(gemstoneType, itemCategory, quality, this, baseModifier)
        .withItemTooltip(isItemTooltip)
        .build();
  }

  public <C extends ModifierConfigBase> C getConfig(Class<C> expectedClass) {
    if (!expectedClass.isInstance(config)) {
      throw new ClassCastException(
          String.format("Expected config type %s, but got %s",
              expectedClass.getSimpleName(),
              config.getClass().getSimpleName()));
    }

    return expectedClass.cast(config);
  }
}
