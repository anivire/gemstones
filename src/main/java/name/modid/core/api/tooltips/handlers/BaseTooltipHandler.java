package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import name.modid.core.utils.tooltip.BoostedValueFormatter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@SuppressWarnings("unchecked")
public abstract class BaseTooltipHandler<T extends ModifierConfig> implements TooltipHandler {
  protected final TooltipBuilder builder;
  protected final T config;
  protected final GemstoneQuality rarityType;

  public BaseTooltipHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    this.builder = builder;
    this.config = (T) config;
    this.rarityType = rarityType;
  }

  @Override
  public MutableText buildTooltip() {
    double raw = extractValue(config);
    ModifierConfig baseConfig = builder.getBaseConfig();
    double displayRaw = raw;
    T typedBaseConfig = null;

    if (baseConfig != null && baseConfig.getClass() == config.getClass()) {
      typedBaseConfig = (T) baseConfig;
      displayRaw = extractValue(typedBaseConfig);
    }

    boolean isPositive = displayRaw > 0;
    String formatted = builder.formatValue(
        Math.abs(adjustValue(config, displayRaw)),
        getPostfix(config));

    MutableText valueText = Text.empty()
        .append(builder.getArrowPrefix(isPositive))
        .append(Text.literal(formatted).formatted(isPositive ? Formatting.GREEN : Formatting.RED));

    if (typedBaseConfig != null) {
      double baseRaw = extractValue(typedBaseConfig);
      double baseAdjusted = Math.abs(adjustValue(typedBaseConfig, baseRaw));
      double boostedAdjusted = Math.abs(adjustValue(config, raw));

      if (BoostedValueFormatter.isBoosted(baseAdjusted, boostedAdjusted)) {
        String boostedFormatted = builder.formatValue(boostedAdjusted, getPostfix(config));
        valueText.append(Text.literal(" (").formatted(Formatting.DARK_GRAY))
            .append(builder.getArrowPrefix(raw > 0, Formatting.LIGHT_PURPLE))
            .append(Text.literal(boostedFormatted).formatted(Formatting.LIGHT_PURPLE))
            .append(Text.literal(")").formatted(Formatting.DARK_GRAY));
      }
    }

    return buildText(config, valueText, isPositive);
  }

  protected abstract double extractValue(T cfg);

  protected double adjustValue(T cfg, double value) {
    return value;
  }

  protected String getPostfix(T cfg) {
    return "";
  }

  protected abstract MutableText buildText(T cfg, MutableText valueText, boolean isPositive);
}
