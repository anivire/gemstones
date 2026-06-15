package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import name.modid.core.api.tooltips.TooltipHelper;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import name.modid.core.utils.oreVision.OreVisionRadius;
import name.modid.core.utils.tooltip.BoostedValueFormatter;
import name.modid.core.utils.witherGuard.WitherGuardSkullLimit;
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
  protected double removeValue(ModifierConfig.PlayerConfig cfg) {
    return cfg.values().get(rarityType);
  }

  @Override
  protected double adjustValue(ModifierConfig.PlayerConfig cfg, double value) {
    if (cfg.eventType() == EventType.PLAYER_WITHER_GUARD) {
      return WitherGuardSkullLimit.fromValue(value);
    }

    if (cfg.eventType() == EventType.PLAYER_TICK_ORE_VISION) {
      return OreVisionRadius.fromValue(value);
    }

    return value * 100;
  }

  @Override
  protected String getPostfix(ModifierConfig.PlayerConfig cfg) {
    if (cfg.eventType() == EventType.PLAYER_WITHER_GUARD) {
      return "";
    }

    if (cfg.eventType() == EventType.PLAYER_TICK_ORE_VISION) {
      return "";
    }

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
      secondArg = valueText;
    } else if (type == EventType.PLAYER_PROJECTILE_IMMUNE) {
      firstArg = valueText.copy()
          .append(Text.literal(" "))
          .append(TooltipHelper.buildTextWithIcon(InlineIcons.HALF_HEART,
              Text.translatable("tooltip.gemstones.unit.health").getString().trim()));
    } else if (type == EventType.PLAYER_RANDOM_EFFECT) {
      double chance = cfg.additionalValues().get(rarityType);
      double seconds = cfg.values().get(rarityType);

      firstArg = TooltipHelper.buildChanceText(builder, chance, isPositive, Formatting.GREEN);
      secondArg = builder.getEventText(type);
      thirdArg = TooltipHelper.buildSecondsText(builder, seconds, Formatting.GREEN);
    } else if (type == EventType.PLAYER_SAVE_LETHAL) {
      double seconds = cfg.values().get(rarityType);

      firstArg = buildSaveLethalThresholdText(cfg);
      secondArg = builder.getEventText(type);
      thirdArg = TooltipHelper.buildSecondsText(builder, seconds, null);
    } else if (type == EventType.PLAYER_TICK_ORE_VISION) {
      firstArg = secondArg;
      secondArg = valueText;
    } else if (type == EventType.ITEM_EXPLOSION_IMMUNE) {
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

  private MutableText buildSaveLethalThresholdText(ModifierConfig.PlayerConfig cfg) {
    double boostedThreshold = cfg.additionalValues().get(rarityType);
    double displayThreshold = boostedThreshold;

    ModifierConfig baseConfig = builder.getBaseConfig();
    if (baseConfig instanceof ModifierConfig.PlayerConfig basePlayerConfig
        && basePlayerConfig.eventType() == EventType.PLAYER_SAVE_LETHAL) {
      displayThreshold = basePlayerConfig.additionalValues().get(rarityType);
    }

    MutableText thresholdText = TooltipHelper.buildTextWithIcon(InlineIcons.HALF_HEART,
        TooltipHelper.formatHealth(builder, displayThreshold));

    if (BoostedValueFormatter.isBoosted(displayThreshold, boostedThreshold)) {
      thresholdText.append(Text.literal(" (").formatted(Formatting.DARK_GRAY))
          .append(Text.literal(TooltipHelper.formatHealth(builder, boostedThreshold))
              .formatted(Formatting.LIGHT_PURPLE))
          .append(Text.literal(")").formatted(Formatting.DARK_GRAY));
    }

    return thresholdText;
  }
}
