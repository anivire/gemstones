package name.modid.core.api.modifiers.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.tooltips.TooltipBuilder;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class PlayerHandler extends BaseTooltipHandler<ModifierConfig.PlayerConfig> {
  public PlayerHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(ModifierConfig.PlayerConfig cfg) {
    return cfg.values().get(rarityType);
  }

  @Override
  protected double adjustValue(ModifierConfig.PlayerConfig cfg, double value) {
    return (cfg.eventType() == EventType.ON_DROP_INCREASE_MOSSY_BOX_DROP
        || cfg.eventType() == EventType.WORLD_EVENT_INCREASE_MOB_SPAWNRATE)
            ? value * 100
            : value;
  }

  @Override
  protected String getPostfix(ModifierConfig.PlayerConfig cfg) {
    return switch (cfg.eventType()) {
      case PLAYER_EVENT_POTION_DURATION -> " seconds";
      case ON_DROP_INCREASE_MOSSY_BOX_DROP, WORLD_EVENT_INCREASE_MOB_SPAWNRATE -> "%";
      default -> "";
    };
  }

  @Override
  protected MutableText buildText(ModifierConfig.PlayerConfig cfg, MutableText valueText, boolean isPositive) {
    return Text
        .translatable(builder.getTranslationKeyByEvent(cfg.eventType()), valueText,
            builder.getEventText(cfg.eventType()))
        .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}