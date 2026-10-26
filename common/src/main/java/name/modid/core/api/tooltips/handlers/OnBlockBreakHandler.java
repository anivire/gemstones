package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.handlers.BlockBreakHandler;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class OnBlockBreakHandler extends BaseTooltipHandler<ModifierConfig.BlockBreakConfig> {
  public OnBlockBreakHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double removeValue(ModifierConfig.BlockBreakConfig cfg) {
    if (cfg.eventType() == EventType.ON_BLOCK_BREAK_MINER) {
      return cfg.additionalValues().get(rarityType);
    }

    return cfg.values().get(rarityType);
  }

  @Override
  protected double adjustValue(ModifierConfig.BlockBreakConfig cfg, double value) {
    if (cfg.eventType() == EventType.ON_BLOCK_BREAK_MINER) {
      return BlockBreakHandler.getMinerRadius(value) * 2 + 1;
    }

    return value * 100;
  }

  @Override
  protected String getPostfix(ModifierConfig.BlockBreakConfig cfg) {
    if (cfg.eventType() == EventType.ON_BLOCK_BREAK_MINER) {
      return "";
    }

    return "%";
  }

  @Override
  protected String formatAdjusted(ModifierConfig.BlockBreakConfig cfg, double adjusted) {
    if (cfg.eventType() == EventType.ON_BLOCK_BREAK_MINER) {
      int side = (int) adjusted;
      return side + "x" + side;
    }

    return super.formatAdjusted(cfg, adjusted);
  }

  @Override
  protected MutableText buildText(ModifierConfig.BlockBreakConfig cfg, MutableText valueText, boolean isPositive) {
    // The magnet always works, a chance value would only be noise
    if (cfg.eventType() == EventType.ON_BLOCK_BREAK_MAGNET) {
      return Text.translatable(
          builder.getTranslationKeyByEvent(cfg.eventType()),
          builder.getEventText(cfg.eventType()))
          .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
    }

    if (cfg.eventType() == EventType.ON_BLOCK_BREAK_EXTRA_HEALTH) {
      return Text.translatable(
          builder.getTranslationKeyByEvent(cfg.eventType()),
          valueText,
          builder.getEventText(cfg.eventType(),
              builder.formatValue(cfg.additionalValues().get(rarityType), "")))
          .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
    }

    if (cfg.eventType() == EventType.ON_BLOCK_BREAK_CHAIN_LIGHTNING) {
      // Whole blocks only, truncated the same way the mechanic reads the value
      ModifierConfig.BlockBreakConfig base = builder.getBaseConfig() instanceof
          ModifierConfig.BlockBreakConfig baseCfg ? baseCfg : cfg;
      int baseBlocks = base.additionalValues().get(rarityType).intValue();
      int blocks = cfg.additionalValues().get(rarityType).intValue();

      MutableText blocksText = Text.literal(String.valueOf(baseBlocks)).formatted(Formatting.GREEN);
      if (blocks != baseBlocks) {
        blocksText.append(Text.literal(" (").formatted(Formatting.DARK_GRAY))
            .append(builder.getArrowPrefix(blocks > baseBlocks, Formatting.LIGHT_PURPLE))
            .append(Text.literal(String.valueOf(blocks)).formatted(Formatting.LIGHT_PURPLE))
            .append(Text.literal(")").formatted(Formatting.DARK_GRAY));
      }

      return Text.translatable(
          builder.getTranslationKeyByEvent(cfg.eventType()),
          valueText,
          builder.getEventText(cfg.eventType()),
          blocksText)
          .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
    }

    if (cfg.eventType() == EventType.ON_BLOCK_BREAK_MINER) {
      return Text.translatable(
          builder.getTranslationKeyByEvent(cfg.eventType()),
          builder.getEventText(cfg.eventType()),
          valueText)
          .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
    } else {
      return Text.translatable(
          builder.getTranslationKeyByEvent(cfg.eventType()),
          valueText,
          builder.getEventText(cfg.eventType()))
          .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
    }
  }
}
