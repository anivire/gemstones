package name.modid.helpers.modifiers.modifierTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import name.modid.Gemstones;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.GemstoneModifierItemType;
import name.modid.helpers.types.GemstoneRarityType;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModifierAttribute implements GemstoneModifier {
  public Operation operation;
  public GemstoneModifierItemType itemType;
  public ArrayList<Double> modifierValuesList = new ArrayList<Double>();
  public RegistryEntry<EntityAttribute> attr;
  public GemstoneType gemstoneType;
  public GemstoneRarityType rarityType;

  public ModifierAttribute(Operation operation, ArrayList<Double> modifierValuesList,
      GemstoneModifierItemType itemType, RegistryEntry<EntityAttribute> attr,
      GemstoneType gemstoneType) {
    this.operation = operation;
    this.modifierValuesList = new ArrayList<Double>(modifierValuesList);
    this.itemType = itemType;
    this.attr = attr;
    this.gemstoneType = gemstoneType;
  }

  public MutableText getTooltipString(GemstoneRarityType gemstoneRarityType,
      Boolean withCategoryString) {
    Double value = modifierValuesList.get(gemstoneRarityType.getValue());
    String tooltipCategoryType = withCategoryString
        ? String.format("tooltip.gemstones.%s_type", itemType.toString().toLowerCase())
        : "tooltip.gemstones.without_type";
    MutableText attributeBonusString = Text.empty();

    if (this.attr == EntityAttributes.GENERIC_MAX_HEALTH) {
      attributeBonusString
          .append(Text.literal("\uE001").styled(
              style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "gemstone_sprite_icons"))))
          .formatted(Formatting.WHITE);
    }

    String percent = this.operation == Operation.ADD_VALUE ? "" : "%";
    Double adjustedValue = this.operation == Operation.ADD_VALUE ? value : value * 100;
    String formattedValue = formatValue(adjustedValue) + percent;
    MutableText resultTooltip = Text.empty();

    return resultTooltip.append(Text.translatable(tooltipCategoryType).formatted(Formatting.GRAY))
        .append(Text.translatable(
            String.format("tooltip.gemstones.%s.%s_bonus",
                this.gemstoneType.toString().toLowerCase(), this.itemType.toString().toLowerCase()),
            Text.literal(formattedValue).formatted(Formatting.BLUE).append(attributeBonusString))
            .formatted(Formatting.GOLD));
  }

  private String formatValue(double value) {
    BigDecimal bd =
        BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
    return bd.toPlainString();
  }

  public GemstoneType getGemstoneType() {
    return this.gemstoneType;
  }

  public GemstoneRarityType getRarityType() {
    return this.rarityType;
  }

  public void setRarityType(GemstoneRarityType rarityType) {
    this.rarityType = rarityType;
  }
}
