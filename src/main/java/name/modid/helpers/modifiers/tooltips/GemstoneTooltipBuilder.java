package name.modid.helpers.modifiers.tooltips;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import name.modid.Gemstones;
import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.category.ModifierAttribute;
import name.modid.helpers.modifiers.category.ModifierMultiplyAttribute;
import name.modid.helpers.modifiers.category.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.category.ModifierOnDamage;
import name.modid.helpers.modifiers.category.ModifierOnFirstHit;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.tooltips.GemstoneTooltipHelper.Icons;
import name.modid.helpers.modifiers.tooltips.GemstoneTooltipHelper.InlineIcons;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class GemstoneTooltipBuilder {
  public enum ModifierCategoryType {
    ATTRIBUTE,
    MULTIPLY_ATTRIBUTE,
    ON_FIRST_HIT,
    ON_DAMAGE,
    ON_BLOCK_BREAK,
    UNDEFINED,
  }

  private final Formatting DEFAULT_TEXT_COLOR = Formatting.GOLD;

  protected final GemstoneType gemstoneType;
  protected final ModifierItemCategory itemCategory;
  protected final GemstoneRarity rarityType;
  protected final ModifierCategoryType modifierCategory;
  protected final GemstoneModifier modifier;
  protected Boolean isItemTooltip = false;

  public GemstoneTooltipBuilder(GemstoneType gemstoneType, ModifierItemCategory itemCategory,
      GemstoneRarity rarityType, GemstoneModifier modifier) {
    this.gemstoneType = gemstoneType;
    this.itemCategory = itemCategory;
    this.rarityType = rarityType;
    this.modifier = modifier;
    this.modifierCategory = this.resolveType(modifier);
  }

  protected ModifierCategoryType resolveType(GemstoneModifier modifier) {
    if (modifier instanceof ModifierAttribute)
      return ModifierCategoryType.ATTRIBUTE;
    if (modifier instanceof ModifierMultiplyAttribute)
      return ModifierCategoryType.MULTIPLY_ATTRIBUTE;
    if (modifier instanceof ModifierOnFirstHit)
      return ModifierCategoryType.ON_FIRST_HIT;
    if (modifier instanceof ModifierOnDamage)
      return ModifierCategoryType.ON_DAMAGE;
    if (modifier instanceof ModifierOnBlockBreak)
      return ModifierCategoryType.ON_BLOCK_BREAK;

    return ModifierCategoryType.UNDEFINED;
  }

  private String formatValue(Double value, String percentString) {
    BigDecimal v = BigDecimal.valueOf(value)
        .setScale(2, RoundingMode.HALF_UP)
        .stripTrailingZeros();

    if (percentString.isBlank()) {
      return v.toPlainString();
    } else {
      return v.toPlainString() + "%";
    }
  }

  private String getTranslationKey() {
    return String.format("tooltip.gemstones.%s.%s_bonus",
        this.gemstoneType.toString().toLowerCase(),
        this.itemCategory.toString().toLowerCase());
  }

  public GemstoneTooltipBuilder withItemTooltip(Boolean flag) {
    this.isItemTooltip = flag;
    return this;
  }

  public MutableText build() {
    String tooltipItemType = isItemTooltip
        ? String.format("tooltip.gemstones.%s_type", this.itemCategory.toString().toLowerCase())
        : "tooltip.gemstones.without_type";

    // Item or gemstone item category prefix
    MutableText tooltipItemPrefix = Text.translatable(tooltipItemType).formatted(Formatting.GRAY);

    switch (modifierCategory) {
      case ATTRIBUTE -> {
        ModifierAttribute m = (ModifierAttribute) modifier;

        Double value = m.getLevelValues().get(this.rarityType);
        Boolean isPositive = value > 0;
        Double adjustedValue = m.getOperation().equals(Operation.ADD_VALUE) ? value : value * 100;
        String percentString = m.getOperation().equals(Operation.ADD_VALUE) ? "" : "%";
        String formattedValue = this.formatValue(Math.abs(adjustedValue), percentString);

        Formatting prefixColor = isPositive ? Formatting.GREEN : Formatting.RED;
        MutableText arrowPrefix = Text
            .literal(isPositive ? InlineIcons.ARROW_UP.getSymbol() : InlineIcons.ARROW_DOWN.getSymbol())
            .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
            .formatted(prefixColor);

        MutableText attributeText = Text
            .empty()
            .append(arrowPrefix)
            .append(Text.literal(formattedValue).formatted(prefixColor));
        String translationKey = this.getTranslationKey();

        return tooltipItemPrefix.append(Text.translatable(translationKey, attributeText).formatted(DEFAULT_TEXT_COLOR));
      }
      case MULTIPLY_ATTRIBUTE -> {
        ModifierMultiplyAttribute multiplyAttribute = (ModifierMultiplyAttribute) modifier;
        ArrayList<MutableText> attributeParts = new ArrayList<>();

        for (ModifierAttribute attr : multiplyAttribute.getInstances()) {
          Double value = attr.getLevelValues().get(this.rarityType);
          boolean isPositive = value > 0;
          Double adjustedValue = attr.getOperation().equals(Operation.ADD_VALUE) ? value : value * 100;
          String percentString = attr.getOperation().equals(Operation.ADD_VALUE) ? "" : "%";
          String formattedValue = this.formatValue(Math.abs(adjustedValue), percentString);

          Formatting prefixColor = isPositive ? Formatting.GREEN : Formatting.RED;
          MutableText arrowPrefix = Text
              .literal(isPositive ? InlineIcons.ARROW_UP.getSymbol() : InlineIcons.ARROW_DOWN.getSymbol())
              .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
              .formatted(prefixColor);

          MutableText attributeText = Text.empty()
              .append(arrowPrefix)
              .append(Text.literal(formattedValue).formatted(prefixColor));

          attributeParts.add(attributeText);
        }

        String translationKey = this.getTranslationKey();

        return tooltipItemPrefix.append(
            Text.translatable(translationKey, attributeParts.toArray())
                .formatted(DEFAULT_TEXT_COLOR));
      }
      default -> {
        return tooltipItemPrefix.append(Text.literal("Undefined bonus").formatted(Formatting.RED));
      }
    }
  }
}
