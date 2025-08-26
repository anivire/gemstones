package name.modid.helpers.modifiers.tooltips;

import name.modid.Gemstones;
import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.category.ModifierAttribute;
import name.modid.helpers.modifiers.category.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.category.ModifierOnDamage;
import name.modid.helpers.modifiers.category.ModifierOnFirstHit;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.tooltips.GemstoneTooltipHelper.Icons;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class GemstoneTooltipBuilderLegacy {
  public enum ModifierTooltipType {
    ATTRIBUTE,
    ON_FIRST_HIT,
    ON_DAMAGE,
    ON_BLOCK_BREAK
  }

  private final GemstoneType gemstoneType;
  private final ModifierItemCategory itemType;
  private final GemstoneModifier modifier;
  private final ModifierTooltipType tooltipType;

  private boolean withCategoryString = false;

  public GemstoneTooltipBuilderLegacy(
      GemstoneType gemstoneType,
      ModifierItemCategory itemType,
      GemstoneModifier modifier) {
    this.gemstoneType = gemstoneType;
    this.itemType = itemType;
    this.modifier = modifier;
    this.tooltipType = resolveType(modifier);
  }

  private ModifierTooltipType resolveType(GemstoneModifier modifier) {
    if (modifier instanceof ModifierAttribute)
      return ModifierTooltipType.ATTRIBUTE;
    if (modifier instanceof ModifierOnFirstHit)
      return ModifierTooltipType.ON_FIRST_HIT;
    if (modifier instanceof ModifierOnDamage)
      return ModifierTooltipType.ON_DAMAGE;
    if (modifier instanceof ModifierOnBlockBreak)
      return ModifierTooltipType.ON_BLOCK_BREAK;
    throw new IllegalArgumentException("Unknown modifier type: " + modifier.getClass());
  }

  public GemstoneTooltipBuilder withCategoryString(boolean withCategory) {
    this.withCategoryString = withCategory;
    return this;
  }

  public MutableText build(GemstoneRarity rarity) {
    String tooltipCategoryType = withCategoryString
        ? String.format("tooltip.gemstones.%s_type", this.itemType.toString().toLowerCase())
        : "tooltip.gemstones.without_type";

    MutableText tooltip = Text.empty()
        .append(Text.translatable(tooltipCategoryType).formatted(Formatting.GRAY));

    switch (tooltipType) {
      case ATTRIBUTE -> buildAttributeTooltip(tooltip, rarity);
      case ON_FIRST_HIT -> {
        ModifierOnFirstHit m = (ModifierOnFirstHit) modifier;
        double value = m.values.get(rarity.getValue()) * 100;
        appendValueTooltip(tooltip, value, Formatting.GREEN);
      }
      case ON_DAMAGE -> {
        ModifierOnDamage m = (ModifierOnDamage) modifier;
        double value = m.value.get(rarity.getValue()) * 100;
        appendValueTooltip(tooltip, value, Formatting.GREEN);
      }
      case ON_BLOCK_BREAK -> {
        ModifierOnBlockBreak m = (ModifierOnBlockBreak) modifier;
        double value = m.value.get(rarity.getValue()) * 100;
        appendValueTooltip(tooltip, value, Formatting.GREEN);
      }
    }

    return tooltip;
  }

  private void buildAttributeTooltip(MutableText tooltip, GemstoneRarity rarity) {
    ModifierAttribute m = (ModifierAttribute) modifier;
    double pureValue = m.modifierValuesList.get(rarity.getValue());

    double value = Math.abs(pureValue);
    String percent = m.operation == Operation.ADD_VALUE ? "" : "%";
    double adjustedValue = m.operation == Operation.ADD_VALUE ? value : value * 100;
    String formattedValue = formatValue(adjustedValue) + percent;

    boolean isPositive = pureValue > 0;
    Formatting valueColor = isPositive ? Formatting.GREEN : Formatting.RED;
    String icon = isPositive ? "\uE006" : "\uE012";

    MutableText modifierText = Text.empty()
        .append(Text.literal(icon)
            .styled(style -> style.withFont(
                Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
            .formatted(valueColor))
        .append(Text.literal(formattedValue).formatted(valueColor));

    String translationKey = getTranslationKey();
    tooltip.append(Text.translatable(translationKey, modifierText).formatted(Formatting.GOLD));
  }

  private void appendValueTooltip(MutableText tooltip, double value, Formatting color) {
    MutableText icon = Text.literal("\uE006")
        .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "icons_font")))
        .formatted(color);

    String translationKey = getTranslationKey();
    tooltip.append(Text.translatable(translationKey,
        icon.append(Text.literal(String.format("%.0f%%", value)).formatted(color)))
        .formatted(Formatting.GOLD));
  }

  private String getTranslationKey() {
    return String.format("tooltip.gemstones.%s.%s_bonus",
        this.gemstoneType.toString().toLowerCase(),
        this.itemType.toString().toLowerCase());
  }

  private String formatValue(double value) {
    if (value == (long) value) {
      return String.format("%d", (long) value);
    } else {
      return String.format("%.2f", value);
    }
  }
}