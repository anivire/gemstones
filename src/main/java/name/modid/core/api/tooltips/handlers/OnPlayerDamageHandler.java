package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import name.modid.core.api.tooltips.TooltipHelper;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import net.minecraft.text.MutableText;

public class OnPlayerDamageHandler extends BaseTooltipHandler<ModifierConfig.OnPlayerDamageConfig> {

  public OnPlayerDamageHandler(TooltipBuilder builder,
      ModifierConfig config,
      GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(ModifierConfig.OnPlayerDamageConfig cfg) {
    return cfg.values().get(rarityType);
  }

  @Override
  protected double adjustValue(ModifierConfig.OnPlayerDamageConfig cfg, double value) {
    return value * 100;
  }

  @Override
  protected String getPostfix(ModifierConfig.OnPlayerDamageConfig cfg) {
    return "%";
  }

  @Override
  protected MutableText buildText(
      ModifierConfig.OnPlayerDamageConfig cfg,
      MutableText valueText,
      boolean isPositive) {
    EventType type = cfg.eventType();
    MutableText firstArg;
    MutableText secondArg = builder.getEventText(type);
    MutableText thirdArg = builder.getEventText(type);

    if (type == EventType.PLAYER_SAVE_LETHAL) {
      double hpThreshold = cfg.additionalValues().get(rarityType);
      double seconds = cfg.values().get(rarityType);

      firstArg = TooltipHelper.buildTextWithIcon(InlineIcons.HALF_HEART,
          builder.formatValue(hpThreshold, " Health"));
      secondArg = builder.getEventText(type);
      thirdArg = TooltipHelper.buildSecondsText(builder, seconds, null);
    } else {
      firstArg = valueText;
    }

    return TooltipHelper.safeTranslatable(
        builder.getTranslationKeyByEvent(type),
        firstArg,
        secondArg,
        thirdArg).formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}