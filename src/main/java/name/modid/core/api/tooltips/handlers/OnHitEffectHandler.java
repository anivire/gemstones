package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierCategoryType;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class OnHitEffectHandler<T extends ModifierConfig> extends BaseTooltipHandler<T> {
  private final boolean projectile;

  public OnHitEffectHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType,
      boolean projectile) {
    super(builder, config, rarityType);
    this.projectile = projectile;
  }

  @Override
  protected double extractValue(T cfg) {
    if (cfg instanceof ModifierConfig.HitEffectMeleeConfig melee) {
      return melee.chance().get(rarityType);
    } else if (cfg instanceof ModifierConfig.HitEffectProjectileConfig proj) {
      return proj.chance().get(rarityType);
    } else {
      return 0;
    }
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
    MutableText effectText = Text.empty();

    if (cfg instanceof ModifierConfig.HitEffectMeleeConfig melee) {
      effectText.append(melee.effect().value().getName())
          .formatted(melee.effect().value().isBeneficial() ? Formatting.GREEN : Formatting.RED);
    } else if (cfg instanceof ModifierConfig.HitEffectProjectileConfig proj) {
      effectText.append(proj.effect().value().getName())
          .formatted(proj.effect().value().isBeneficial() ? Formatting.GREEN : Formatting.RED);
    }

    return Text.translatable(
        builder.getTranslationKeyByModifier(projectile
            ? ModifierCategoryType.ON_HIT_EFFECT_PROJECTILE
            : ModifierCategoryType.ON_HIT_EFFECT_MELEE),
        valueText,
        effectText).formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}