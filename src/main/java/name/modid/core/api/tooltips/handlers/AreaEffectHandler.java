package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierCategoryType;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AreaEffectHandler extends BaseTooltipHandler<ModifierConfig.AreaEffectConfig> {
  public AreaEffectHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(ModifierConfig.AreaEffectConfig cfg) {
    return cfg.radiusLevels().get(rarityType);
  }

  @Override
  protected String getPostfix(ModifierConfig.AreaEffectConfig cfg) {
    return " blocks";
  }

  @Override
  protected MutableText buildText(ModifierConfig.AreaEffectConfig cfg, MutableText valueText, boolean isPositive) {
    MutableText effectText = Text.empty()
        .append(cfg.effect().value().getName())
        .formatted(cfg.effect().value().isBeneficial() ? Formatting.GREEN : Formatting.RED);

    return Text.translatable(
        builder.getTranslationKeyByModifier(ModifierCategoryType.AREA_EFFECT),
        effectText,
        valueText).formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}