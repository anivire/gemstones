package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import name.modid.core.api.tooltips.TooltipHelper;
import net.minecraft.text.MutableText;

public class OnHitHandler<T extends ModifierConfig> extends BaseTooltipHandler<T> {
  private final boolean projectile;

  public OnHitHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType, boolean projectile) {
    super(builder, config, rarityType);
    this.projectile = projectile;
  }

  @Override
  protected double extractValue(T cfg) {
    if (cfg instanceof ModifierConfig.HitMeleeConfig melee)
      return melee.chance().get(rarityType);
    if (cfg instanceof ModifierConfig.HitProjectileConfig proj)
      return proj.chance().get(rarityType);
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
    EventType ev = (cfg instanceof ModifierConfig.HitMeleeConfig melee) ? melee.eventType()
        : (cfg instanceof ModifierConfig.HitProjectileConfig proj) ? proj.eventType() : null;

    return TooltipHelper.safeTranslatable(builder.getTranslationKeyByEvent(ev), valueText, builder.getEventText(ev))
        .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}