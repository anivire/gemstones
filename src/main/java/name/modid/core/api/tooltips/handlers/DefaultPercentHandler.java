package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class DefaultPercentHandler<T extends ModifierConfig.Events> extends BaseTooltipHandler<T> {
  public DefaultPercentHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(T cfg) {
    return cfg.values().get(rarityType);
  }

  @Override
  protected double adjustValue(T cfg, double value) {
    return value * 100;
  }

  @Override
  protected String getPostfix(T cfg) {
    return "%";
  }

  @Override
  protected MutableText buildText(T cfg, MutableText valueText, boolean isPositive) {
    return Text
        .translatable(
            builder.getTranslationKeyByEvent(cfg.eventType()), valueText,
            builder.getEventText(cfg.eventType()))
        .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}