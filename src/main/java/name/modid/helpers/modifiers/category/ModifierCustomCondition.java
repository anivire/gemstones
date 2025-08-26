package name.modid.helpers.modifiers.category;

import java.util.ArrayList;

import name.modid.Gemstones;
import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.type.ConditionType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModifierCustomCondition implements GemstoneModifier {
  public ArrayList<Double> value = new ArrayList<Double>();
  public ArrayList<Double> additionalValue = new ArrayList<Double>();
  public ConditionType conditionType;
  public ModifierItemCategory itemType;
  public GemstoneType gemstoneType;
  public GemstoneRarity rarityType;

  public ModifierCustomCondition(ArrayList<Double> value, ArrayList<Double> additionalValue,
      ConditionType conditionType, ModifierItemCategory itemType, GemstoneType gemstoneType) {
    this.value = value;
    this.additionalValue = new ArrayList<Double>(additionalValue);
    this.itemType = itemType;
    this.gemstoneType = gemstoneType;
    this.conditionType = conditionType;
  }

  public MutableText getTooltipString(GemstoneRarity gemstoneRarityType,
      Boolean withCategoryString) {
    boolean isPercent = this.conditionType != ConditionType.POTION_DURATION ? true : false;
    Object v = value.get(gemstoneRarityType.getValue()) * (isPercent ? 100 : 1);
    String tooltipCategoryType = withCategoryString
        ? String.format("tooltip.gemstones.%s_type", itemType.toString().toLowerCase())
        : "tooltip.gemstones.without_type";
    MutableText resultTooltip = Text.empty();
    MutableText eventString = Text.empty();

    if (this.conditionType == ConditionType.POTION_DURATION) {

    }

    return resultTooltip.append(Text.translatable(tooltipCategoryType).formatted(Formatting.GRAY))
        .append(Text.literal("\uE006").styled(
            style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "icons_font"))))
        .formatted(Formatting.GREEN)
        .append(Text.translatable(
            String.format("tooltip.gemstones.%s.%s_bonus", gemstoneType.toString().toLowerCase(),
                itemType.toString().toLowerCase()),
            Text.literal(String.format("%.0f", v) + (isPercent ? "%" : " seconds")).formatted(Formatting.GREEN),
            eventString)
            .formatted(Formatting.GOLD));
  }

  public GemstoneType getGemstoneType() {
    return this.gemstoneType;
  }

  public GemstoneRarity getRarityType() {
    return this.rarityType;
  }

  public void setRarityType(GemstoneRarity rarityType) {
    this.rarityType = rarityType;
  }

  public ModifierItemCategory getItemCategory() {
    return this.itemType;
  };
}
