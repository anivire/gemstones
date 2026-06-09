package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class OnPotionBrewHandler extends BaseTooltipHandler<ModifierConfig.OnPotionBrewConfig> {
  public OnPotionBrewHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double removeValue(ModifierConfig.OnPotionBrewConfig cfg) {
    return cfg.values().get(rarityType);
  }

  @Override
  protected String getPostfix(ModifierConfig.OnPotionBrewConfig cfg) {
    return Text.translatable("tooltip.gemstones.unit.second").getString();
  }

  @Override
  protected MutableText buildText(ModifierConfig.OnPotionBrewConfig cfg, MutableText valueText, boolean isPositive) {
    return Text.translatable(
        builder.getTranslationKeyByEvent(cfg.eventType()), valueText,
        builder.getEventText(cfg.eventType())).formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}