package name.modid.core.api.modifiers.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.tooltips.TooltipBuilder;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class OnFirstHitHandler extends BaseTooltipHandler<ModifierConfig.OnFirstHitConfig> {
  public OnFirstHitHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(ModifierConfig.OnFirstHitConfig cfg) {
    return cfg.values().get(rarityType);
  }

  @Override
  protected double adjustValue(ModifierConfig.OnFirstHitConfig cfg, double value) {
    return value * 100;
  }

  @Override
  protected String getPostfix(ModifierConfig.OnFirstHitConfig cfg) {
    return "%";
  }

  @Override
  protected MutableText buildText(ModifierConfig.OnFirstHitConfig cfg, MutableText valueText, boolean isPositive) {
    return Text
        .translatable(builder.getTranslationKeyByEvent(cfg.eventType()), valueText,
            builder.getEventText(cfg.eventType()))
        .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}