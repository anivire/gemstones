package name.modid.core.api.tooltips.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.ModifierCategoryType;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.TooltipBuilder;
import name.modid.core.utils.tooltip.BoostedValueFormatter;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MultiplyAttributeHandler extends BaseTooltipHandler<ModifierConfig.MultiplyAttributeConfig> {
  public MultiplyAttributeHandler(
      TooltipBuilder builder,
      ModifierConfig config,
      GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(ModifierConfig.MultiplyAttributeConfig cfg) {
    return 0;
  }

  @Override
  public MutableText buildTooltip() {
    List<MutableText> parts = new ArrayList<>();
    List<ModifierConfig.AttributeConfig> baseInstances = List.of();

    if (builder.getBaseConfig() instanceof ModifierConfig.MultiplyAttributeConfig baseMulti) {
      baseInstances = baseMulti.instances();
    }

    for (int i = 0; i < config.instances().size(); i++) {
      ModifierConfig.AttributeConfig sub = config.instances().get(i);
      ModifierConfig.AttributeConfig baseSub = i < baseInstances.size() ? baseInstances.get(i) : sub;
      double value = sub.values().get(rarityType);
      double displayValue = baseSub.values().get(rarityType);
      double displayAdjusted = adjustValue(sub, displayValue);
      double boostedAdjusted = adjustValue(sub, value);
      String postfix = sub.operation() == Operation.ADD_VALUE ? "" : "%";
      String formatted = builder.formatValue(Math.abs(displayAdjusted), postfix);
      boolean isPositive = displayValue > 0;

      MutableText part = Text.empty()
          .append(builder.getArrowPrefix(isPositive))
          .append(Text.literal(formatted).formatted(isPositive ? Formatting.GREEN : Formatting.RED));

      if (BoostedValueFormatter.isBoosted(Math.abs(displayAdjusted), Math.abs(boostedAdjusted))) {
        part.append(Text.literal(" (").formatted(Formatting.DARK_GRAY))
            .append(builder.getArrowPrefix(value > 0, Formatting.LIGHT_PURPLE))
            .append(Text.literal(builder.formatValue(Math.abs(boostedAdjusted), postfix)).formatted(Formatting.LIGHT_PURPLE))
            .append(Text.literal(")").formatted(Formatting.DARK_GRAY));
      }

      part.append(" ")
          .append(Text.translatable(sub.attribute().value().getTranslationKey().toLowerCase())
              .formatted(Formatting.BLUE));

      parts.add(part);
    }

    MutableText combined = Text.empty();

    for (int i = 0; i < parts.size(); i++) {
      combined.append(parts.get(i));
      if (i < parts.size() - 1) {
        combined.append(Text.translatable("tooltip.gemstones.multiply_attribute.split").getString());
      }
    }

    return Text.translatable(
        builder.getTranslationKeyByModifier(ModifierCategoryType.MULTIPLY_ATTRIBUTE),
        combined).formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }

  @Override
  protected MutableText buildText(
      ModifierConfig.MultiplyAttributeConfig cfg,
      MutableText valueText,
      boolean isPositive) {
    return Text.empty();
  }

  private double adjustValue(ModifierConfig.AttributeConfig cfg, double value) {
    return cfg.operation() == Operation.ADD_VALUE ? value : value * 100;
  }
}
