package name.modid.core.api.modifiers.tooltips.handlers;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.tooltips.TooltipBuilder;
import name.modid.core.api.modifiers.tooltips.TooltipHelper.Icons;
import name.modid.core.api.modifiers.tooltips.TooltipHelper.InlineIcons;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

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
    return value * 100;
  }

  @Override
  protected String getPostfix(ModifierConfig.PlayerConfig cfg) {
    return "%";
  }

  @Override
  protected MutableText buildText(ModifierConfig.PlayerConfig cfg, MutableText valueText, boolean isPositive) {
    MutableText s = Text.literal(" ").styled(style -> style.withFont(Style.DEFAULT_FONT_ID));
    MutableText t = Text.literal(InlineIcons.HALF_HEART.getSymbol())
        .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);
    MutableText i = Text.literal(" Health").styled(style -> style.withFont(Style.DEFAULT_FONT_ID))
        .formatted(Formatting.RED);
    s.append(t).append(i);

    return Text
        .translatable(builder.getTranslationKeyByEvent(cfg.eventType()),
            cfg.eventType() == EventType.PLAYER_WITHER_GUARD ? builder.getEventText(cfg.eventType())
                : valueText.append(cfg.eventType() == EventType.PLAYER_PROJECTILE_IMMUNE ? s : null),
            builder.getEventText(cfg.eventType()))
        .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}