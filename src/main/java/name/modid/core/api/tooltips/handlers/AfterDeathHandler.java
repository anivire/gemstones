package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class AfterDeathHandler extends BaseTooltipHandler<ModifierConfig.AfterDeathConfig> {
  public AfterDeathHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(ModifierConfig.AfterDeathConfig cfg) {
    return cfg.values().get(rarityType);
  }

  @Override
  protected double adjustValue(ModifierConfig.AfterDeathConfig cfg, double value) {
    return value * 100;
  }

  @Override
  protected String getPostfix(ModifierConfig.AfterDeathConfig cfg) {
    return "%";
  }

  @Override
  protected MutableText buildText(ModifierConfig.AfterDeathConfig cfg, MutableText valueText, boolean isPositive) {
    return Text
        .translatable(builder.getTranslationKeyByEvent(cfg.eventType()), valueText,
            builder.getEventText(cfg.eventType()))
        .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}