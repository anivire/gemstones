package name.modid.core.api.modifiers.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.tooltips.TooltipBuilder;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class CustomConditionHandler extends BaseTooltipHandler<ModifierConfig.CustomConditionConfig> {
  public CustomConditionHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(ModifierConfig.CustomConditionConfig cfg) {
    return cfg.value().get(rarityType);
  }

  @Override
  protected double adjustValue(ModifierConfig.CustomConditionConfig cfg, double value) {
    return (cfg.eventType() == EventType.INCREASE_MOSSY_BOX_DROP || cfg.eventType() == EventType.INCREASE_MOB_SPAWNRATE)
        ? value * 100
        : value;
  }

  @Override
  protected String getPostfix(ModifierConfig.CustomConditionConfig cfg) {
    return switch (cfg.eventType()) {
      case POTION_DURATION -> " seconds";
      case INCREASE_MOSSY_BOX_DROP, INCREASE_MOB_SPAWNRATE -> "%";
      default -> "";
    };
  }

  @Override
  protected MutableText buildText(ModifierConfig.CustomConditionConfig cfg, MutableText valueText, boolean isPositive) {
    return Text
        .translatable(builder.getTranslationKeyByEvent(cfg.eventType()), valueText,
            builder.getEventText(cfg.eventType()))
        .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}