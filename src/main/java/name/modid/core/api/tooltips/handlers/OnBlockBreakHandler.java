package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class OnBlockBreakHandler extends BaseTooltipHandler<ModifierConfig.BlockBreakConfig> {
  public OnBlockBreakHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(ModifierConfig.BlockBreakConfig cfg) {
    return cfg.values().get(rarityType);
  }

  @Override
  protected double adjustValue(ModifierConfig.BlockBreakConfig cfg, double value) {
    return value * 100;
  }

  @Override
  protected String getPostfix(ModifierConfig.BlockBreakConfig cfg) {
    return "%";
  }

  @Override
  protected MutableText buildText(ModifierConfig.BlockBreakConfig cfg, MutableText valueText, boolean isPositive) {
    if (cfg.eventType() == EventType.ON_BLOCK_BREAK_MINER) {
      return Text.translatable(
          builder.getTranslationKeyByEvent(cfg.eventType()),
          builder.getEventText(cfg.eventType()))
          .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
    } else {
      return Text.translatable(
          builder.getTranslationKeyByEvent(cfg.eventType()),
          valueText,
          builder.getEventText(cfg.eventType()))
          .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
    }
  }
}