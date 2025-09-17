package name.modid.core.api.modifiers.tooltips.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.tooltips.TooltipBuilder;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MultiplyAttributeHandler extends BaseTooltipHandler<ModifierConfig.MultiplyAttributeConfig> {
  public MultiplyAttributeHandler(TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    super(builder, config, rarityType);
  }

  @Override
  protected double extractValue(ModifierConfig.MultiplyAttributeConfig cfg) {
    return 0; // не используется
  }

  @Override
  public MutableText buildTooltip() {
    List<MutableText> parts = new ArrayList<>();

    for (ModifierConfig.AttributeConfig sub : config.instances()) {
      double value = sub.values().get(rarityType);
      boolean isPositive = value > 0;
      double adjusted = sub.operation() == Operation.ADD_VALUE ? value : value * 100;
      String postfix = sub.operation() == Operation.ADD_VALUE ? "" : "%";
      String formatted = builder.formatValue(Math.abs(adjusted), postfix);

      MutableText part = Text.empty()
          .append(builder.getArrowPrefix(isPositive))
          .append(Text.literal(formatted).formatted(isPositive ? Formatting.GREEN : Formatting.RED))
          .append(" ")
          .append(Text.translatable(sub.attribute().value().getTranslationKey().toLowerCase())
              .formatted(Formatting.BLUE));

      parts.add(part);
    }

    String joiner = Text.translatable("tooltip.gemstones.MULTIPLY_ATTRIBUTE.split").getString();

    MutableText combined = Text.empty();
    for (int i = 0; i < parts.size(); i++) {
      combined.append(parts.get(i));
      if (i < parts.size() - 1) {
        combined.append(joiner);
      }
    }

    return Text.translatable(
        builder.getTranslationKeyByModifier(TooltipBuilder.ModifierCategoryType.MULTIPLY_ATTRIBUTE),
        combined).formatted(TooltipBuilder.DEFAULT_TEXT_COLOR);
  }

  @Override
  protected MutableText buildText(ModifierConfig.MultiplyAttributeConfig cfg, MutableText valueText,
      boolean isPositive) {
    return Text.empty();
  }
}