package name.modid.helpers.modifiers.category;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import name.modid.Gemstones;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModifierMultiplyAttribute implements GemstoneModifier {
  public List<ModifierAttribute> instances = new ArrayList<>();

  public ModifierMultiplyAttribute(List<ModifierAttribute> instances) {
    this.instances = new ArrayList<ModifierAttribute>(instances);
  }

  public MutableText getTooltipString(GemstoneRarity gemstoneRarityType,
      Boolean withCategoryString) {
    String tooltipCategoryType = withCategoryString
        ? String.format("tooltip.gemstones.%s_type",
            instances.get(0).itemType.toString().toLowerCase())
        : "tooltip.gemstones.without_type";
    MutableText resultTooltip = Text.empty();
    ArrayList<MutableText> modifierTexts = new ArrayList<>();

    for (ModifierAttribute modifierInstance : instances) {
      Double pureValue = modifierInstance.modifierValuesList.get(gemstoneRarityType.getValue());
      Double value = Math.abs(pureValue);
      MutableText attributeBonus = Text.empty();

      if (modifierInstance.attr == EntityAttributes.GENERIC_MAX_HEALTH) {
        attributeBonus.append(Text.literal("\uE001")
            .styled(
                style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "icons_font")))
            .formatted(Formatting.WHITE));
      }

      String percent = modifierInstance.operation == Operation.ADD_VALUE ? "" : "%";
      Double adjustedValue = modifierInstance.operation == Operation.ADD_VALUE ? value : value * 100;
      String formattedValue = formatValue(adjustedValue) + percent;

      modifierTexts
          .add(Text.empty()
              .append(Text.literal(pureValue > 0 ? "\uE006" : "\uE012").styled(
                  style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "icons_font")))
                  .formatted(pureValue > 0 ? Formatting.GREEN : Formatting.RED))
              .append(Text.literal(formattedValue)
                  .formatted(pureValue > 0 ? Formatting.GREEN : Formatting.RED)
                  .append(attributeBonus)));
    }

    String translationKey = String.format("tooltip.gemstones.%s.%s_bonus",
        instances.get(0).gemstoneType.toString().toLowerCase(),
        instances.get(0).itemType.toString().toLowerCase());

    resultTooltip.append(Text.translatable(tooltipCategoryType).formatted(Formatting.GRAY)).append(
        Text.translatable(translationKey, modifierTexts.toArray()).formatted(Formatting.GOLD));

    return resultTooltip;
  }

  private String formatValue(double value) {
    BigDecimal bd = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
    return bd.toPlainString();
  }

  public GemstoneType getGemstoneType() {
    return null;
  }

  public GemstoneRarity getRarityType() {
    return null;
  }

  public void setRarityType(GemstoneRarity rarityType) {
  }

  public ModifierItemCategory getItemCategory() {
    return this.instances.get(0).itemType;
  };
}
