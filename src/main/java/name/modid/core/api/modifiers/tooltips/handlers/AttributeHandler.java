package name.modid.core.api.modifiers.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierCategoryType;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.tooltips.TooltipBuilder;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AttributeHandler extends BaseTooltipHandler<ModifierConfig.AttributeConfig> {

  public AttributeHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(ModifierConfig.AttributeConfig cfg) {
    return cfg.values().get(rarityType);
  }

  @Override
  protected double adjustValue(ModifierConfig.AttributeConfig cfg, double value) {
    return cfg.operation().equals(Operation.ADD_VALUE) ? value : value * 100;
  }

  @Override
  protected String getPostfix(ModifierConfig.AttributeConfig cfg) {
    return cfg.operation().equals(Operation.ADD_VALUE) ? "" : "%";
  }

  @Override
  protected MutableText buildText(ModifierConfig.AttributeConfig cfg, MutableText valueText, boolean isPositive) {
    return Text.translatable(
        builder.getTranslationKeyByModifier(ModifierCategoryType.ATTRIBUTE),
        valueText,
        Text.translatable(cfg.attribute().value().getTranslationKey().toLowerCase())).formatted(Formatting.BLUE);
  }
}