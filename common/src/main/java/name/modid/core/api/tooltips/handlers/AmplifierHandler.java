package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.AmplifierConfig;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import name.modid.core.api.tooltips.TooltipHelper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AmplifierHandler implements TooltipHandler {
  private final TooltipBuilder builder;
  private final ModifierConfig config;
  private final GemstoneQuality rarityType;

  public AmplifierHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    this.builder = builder;
    this.config = config;
    this.rarityType = rarityType;
  }

  @Override
  public MutableText buildTooltip() {
    if (!(config instanceof AmplifierConfig amplifier)) {
      return TooltipHelper.safeTranslatable("tooltip.gemstones.category.unknown").formatted(Formatting.RED);
    }

    double percent = amplifier.values().get(rarityType) * 100.0;
    MutableText valueText = Text.empty()
        .append(builder.getArrowPrefix(true))
        .append(Text.literal(builder.formatValue(percent, "%")).formatted(Formatting.GREEN));

    if (builder.getBaseConfig() instanceof AmplifierConfig baseAmplifier) {
      double basePercent = baseAmplifier.values().get(rarityType) * 100.0;
      if (Double.compare(basePercent, percent) != 0) {
        valueText = Text.empty()
            .append(builder.getArrowPrefix(true))
            .append(Text.literal(builder.formatValue(basePercent, "%")).formatted(Formatting.GREEN))
            .append(Text.literal(" (").formatted(Formatting.DARK_GRAY))
            .append(builder.getArrowPrefix(true, Formatting.LIGHT_PURPLE))
            .append(Text.literal(builder.formatValue(percent, "%")).formatted(Formatting.LIGHT_PURPLE))
            .append(Text.literal(")").formatted(Formatting.DARK_GRAY));
      }
    }

    return TooltipHelper.safeTranslatable(
            "tooltip.gemstones.amplifier",
            valueText)
        .formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }
}
