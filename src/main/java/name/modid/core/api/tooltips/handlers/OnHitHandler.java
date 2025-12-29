package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.LevelValues;
import name.modid.core.api.tooltips.TooltipBuilder;
import name.modid.core.api.tooltips.TooltipHelper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class OnHitHandler<T extends ModifierConfig> extends BaseTooltipHandler<T> {
  private final boolean projectile;

  public OnHitHandler(
      TooltipBuilder builder,
      ModifierConfig config,
      GemstoneQuality rarityType,
      boolean projectile) {
    super(builder, config, rarityType);
    this.projectile = projectile;
  }

  @Override
  protected double extractValue(T cfg) {
    if (cfg instanceof ModifierConfig.HitMeleeConfig melee)
      return melee.values().get(rarityType);
    if (cfg instanceof ModifierConfig.HitProjectileConfig proj)
      return proj.values().get(rarityType);
    return 0;
  }

  @Override
  protected double adjustValue(T cfg, double value) {
    return value * 100;
  }

  @Override
  protected String getPostfix(T cfg) {
    return "%";
  }

  @Override
  protected MutableText buildText(T cfg, MutableText valueText, boolean isPositive) {
    EventType e = (cfg instanceof ModifierConfig.HitMeleeConfig melee)
        ? melee.eventType()
        : (cfg instanceof ModifierConfig.HitProjectileConfig proj)
            ? proj.eventType()
            : null;
    LevelValues additionalValues = (cfg instanceof ModifierConfig.HitMeleeConfig melee)
        ? melee.additionalValues()
        : (cfg instanceof ModifierConfig.HitProjectileConfig proj)
            ? proj.additionalValues()
            : null;
    MutableText firstArg = valueText;
    MutableText secondArg = builder.getEventText(e);

    if (e == EventType.ON_HIT_MAGIC_STRIKE) {
      return TooltipHelper
          .safeTranslatable(
              builder.getTranslationKeyByEvent(e),
              firstArg,
              secondArg,
              Text.literal("30%").formatted(Formatting.RED))
          .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
    } else if (e == EventType.ON_HIT_EXP_ADDITIONAL_DAMAGE) {
      MutableText levelsNumberText = Text.literal(
          String.valueOf(additionalValues.get(rarityType).intValue()))
          .formatted(Formatting.GREEN);

      MutableText levelsWordText = Text.translatable(
          EventType.ON_HIT_EXP_ADDITIONAL_DAMAGE.getTranslationKey())
          .formatted(Formatting.BLUE);

      return TooltipHelper
          .safeTranslatable(
              builder.getTranslationKeyByEvent(e),
              valueText,
              levelsNumberText,
              levelsWordText)
          .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
    } else {
      return TooltipHelper
          .safeTranslatable(
              builder.getTranslationKeyByEvent(e),
              firstArg,
              secondArg)
          .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
    }
  }
}