package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierCategoryType;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import name.modid.core.utils.tooltip.AttributeValueFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AttributeHandler extends BaseTooltipHandler<ModifierConfig.AttributeConfig> {

  public AttributeHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double removeValue(ModifierConfig.AttributeConfig cfg) {
    return cfg.values().get(rarityType);
  }

  @Override
  protected boolean hasDiminishingStack(ModifierConfig.AttributeConfig cfg) {
    return AttributeValueFormat.hasDiminishingStack(cfg, builder.getItemCategory());
  }

  @Override
  protected double adjustValue(ModifierConfig.AttributeConfig cfg, double value) {
    return AttributeValueFormat.toDisplay(cfg, value);
  }

  @Override
  protected String getPostfix(ModifierConfig.AttributeConfig cfg) {
    return AttributeValueFormat.postfix(cfg);
  }

  @Override
  protected MutableText buildText(ModifierConfig.AttributeConfig cfg, MutableText valueText, boolean isPositive) {
    return Text.translatable(
        builder.getTranslationKeyByModifier(ModifierCategoryType.ATTRIBUTE),
        valueText,
        AttributeValueFormat.name(cfg)).formatted(Formatting.BLUE);
  }
}