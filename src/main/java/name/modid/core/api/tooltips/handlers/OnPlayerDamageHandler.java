package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import name.modid.core.api.tooltips.TooltipHelper;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import name.modid.core.utils.tooltip.BoostedValueFormatter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
      double seconds = cfg.values().get(rarityType);

      firstArg = buildSaveLethalThresholdText(cfg);
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

  private MutableText buildSaveLethalThresholdText(ModifierConfig.OnPlayerDamageConfig cfg) {
    double boostedThreshold = cfg.additionalValues().get(rarityType);
    double displayThreshold = boostedThreshold;

    ModifierConfig baseConfig = builder.getBaseConfig();
    if (baseConfig instanceof ModifierConfig.OnPlayerDamageConfig baseDamageConfig
        && baseDamageConfig.eventType() == EventType.PLAYER_SAVE_LETHAL) {
      displayThreshold = baseDamageConfig.additionalValues().get(rarityType);
    }

    MutableText thresholdText = TooltipHelper.buildTextWithIcon(InlineIcons.HALF_HEART,
        builder.formatValue(displayThreshold, " Health"));

    if (BoostedValueFormatter.isBoosted(displayThreshold, boostedThreshold)) {
      thresholdText.append(Text.literal(" (").formatted(Formatting.DARK_GRAY))
          .append(Text.literal(builder.formatValue(boostedThreshold, " Health")).formatted(Formatting.LIGHT_PURPLE))
          .append(Text.literal(")").formatted(Formatting.DARK_GRAY));
    }

    return thresholdText;
  }
}
