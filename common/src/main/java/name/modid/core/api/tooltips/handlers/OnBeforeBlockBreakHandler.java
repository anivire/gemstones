package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.handlers.BeforeBlockBreakHandler;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class OnBeforeBlockBreakHandler extends DefaultPercentHandler<ModifierConfig.BeforeBlockBreakConfig> {
  public OnBeforeBlockBreakHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected MutableText buildText(
      ModifierConfig.BeforeBlockBreakConfig cfg,
      MutableText valueText,
      boolean isPositive) {
    if (cfg.eventType() != EventType.ON_BLOCK_BREAK_GAMBLE) {
      return super.buildText(cfg, valueText, isPositive);
    }

    // The loss chance follows the shown win chance, unboosted like the value itself
    ModifierConfig.BeforeBlockBreakConfig shown = builder.getBaseConfig() instanceof
        ModifierConfig.BeforeBlockBreakConfig base ? base : cfg;
    double lossChance = shown.values().get(rarityType) * BeforeBlockBreakHandler.GAMBLE_LOSS_RATIO;

    MutableText lossText = Text.empty()
        .append(builder.getArrowPrefix(false))
        .append(Text.literal(builder.formatValue(lossChance * 100, "%")).formatted(Formatting.RED));

    return Text.translatable(
        builder.getTranslationKeyByEvent(cfg.eventType()),
        valueText,
        builder.getEventText(cfg.eventType()),
        lossText)
        .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}
