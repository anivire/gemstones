package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import name.modid.core.api.tooltips.TooltipHelper;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PlayerHandler extends BaseTooltipHandler<ModifierConfig.PlayerConfig> {

  public PlayerHandler(TooltipBuilder builder,
      ModifierConfig config,
      GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(ModifierConfig.PlayerConfig cfg) {
    return cfg.values().get(rarityType);
  }

  @Override
  protected double adjustValue(ModifierConfig.PlayerConfig cfg, double value) {
    return value * 100;
  }

  @Override
  protected String getPostfix(ModifierConfig.PlayerConfig cfg) {
    return "%";
  }

  @Override
  protected MutableText buildText(
      ModifierConfig.PlayerConfig cfg,
      MutableText valueText,
      boolean isPositive) {
    EventType type = cfg.eventType();
    MutableText firstArg;
    MutableText secondArg = builder.getEventText(type);
    MutableText thirdArg = builder.getEventText(type);

    if (type == EventType.PLAYER_WITHER_GUARD) {
      firstArg = secondArg;
    } else if (type == EventType.PLAYER_PROJECTILE_IMMUNE) {
      firstArg = valueText.copy()
          .append(Text.literal(" "))
          .append(TooltipHelper.buildTextWithIcon(InlineIcons.HALF_HEART, "Health"));
    } else if (type == EventType.PLAYER_RANDOM_EFFECT) {
      double chance = cfg.additionalValues().get(rarityType);
      double seconds = cfg.values().get(rarityType);

      firstArg = TooltipHelper.buildChanceText(builder, chance, isPositive, Formatting.GREEN);
      secondArg = builder.getEventText(type);
      thirdArg = TooltipHelper.buildSecondsText(builder, seconds, Formatting.GREEN);
    } else if (type == EventType.PLAYER_SAVE_LETHAL) {
      double hpThreshold = cfg.additionalValues().get(rarityType);
      double seconds = cfg.values().get(rarityType);

      firstArg = TooltipHelper.buildTextWithIcon(InlineIcons.HALF_HEART,
          builder.formatValue(hpThreshold, " Health"));
      secondArg = builder.getEventText(type);
      thirdArg = TooltipHelper.buildSecondsText(builder, seconds, null);
    } else if (type == EventType.PLAYER_TICK_ORE_VISION) {
      firstArg = secondArg;
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