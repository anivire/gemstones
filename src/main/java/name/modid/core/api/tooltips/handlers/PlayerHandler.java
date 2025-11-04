package name.modid.core.api.tooltips.handlers;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import name.modid.core.api.tooltips.TooltipHelper;
import name.modid.core.api.tooltips.TooltipHelper.Icons;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class PlayerHandler extends BaseTooltipHandler<ModifierConfig.PlayerConfig> {

  public PlayerHandler(TooltipBuilder builder,
      ModifierConfig config,
      GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(ModifierConfig.PlayerConfig cfg) {
    return cfg.values().get(rarityType);
  }

  @Override
  protected double adjustValue(ModifierConfig.PlayerConfig cfg, double value) {
    return value * 100;
  }

  @Override
  protected String getPostfix(ModifierConfig.PlayerConfig cfg) {
    return "%";
  }

  @Override
  protected MutableText buildText(ModifierConfig.PlayerConfig cfg,
      MutableText valueText,
      boolean isPositive) {

    MutableText iconHeart = Text.literal(InlineIcons.HALF_HEART.getSymbol())
        .styled(s -> s.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);
    MutableText textHealth = Text.literal(" Health")
        .styled(s -> s.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.RED);
    MutableText healthPart = Text.literal(" ")
        .styled(s -> s.withFont(Style.DEFAULT_FONT_ID))
        .append(iconHeart)
        .append(textHealth);

    MutableText firstArg;
    MutableText secondArg;
    MutableText thirdArg = builder.getEventText(cfg.eventType());

    switch (cfg.eventType()) {
      case PLAYER_WITHER_GUARD -> {
        firstArg = builder.getEventText(cfg.eventType());
        secondArg = thirdArg;
      }
      case PLAYER_PROJECTILE_IMMUNE -> {
        firstArg = valueText.copy().append(healthPart);
        secondArg = thirdArg;
      }
      case PLAYER_RANDOM_EFFECT -> {
        double chance = cfg.additionValues().get(rarityType);
        String chanceText = builder.formatValue(chance * 100, "%");

        MutableText chanceMutable = Text.empty()
            .append(builder.getArrowPrefix(isPositive).copy())
            .append(Text.literal(chanceText)
                .formatted(Formatting.GREEN));

        double seconds = cfg.values().get(rarityType);
        MutableText secondsText = Text.literal(
            builder.formatValue(seconds, " seconds"))
            .formatted(Formatting.GREEN);

        firstArg = secondsText;
        secondArg = chanceMutable;
      }
      default -> {
        firstArg = valueText;
        secondArg = thirdArg;
      }
    }

    return TooltipHelper.safeTranslatable(
        builder.getTranslationKeyByEvent(cfg.eventType()),
        firstArg,
        secondArg,
        thirdArg)
        .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}