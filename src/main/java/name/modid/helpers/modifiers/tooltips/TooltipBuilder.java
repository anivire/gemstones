package name.modid.helpers.modifiers.tooltips;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import name.modid.Gemstones;
import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.category.ModifierAreaEffect;
import name.modid.helpers.modifiers.category.ModifierAttribute;
import name.modid.helpers.modifiers.category.ModifierCustomCondition;
import name.modid.helpers.modifiers.category.ModifierMultiplyAttribute;
import name.modid.helpers.modifiers.category.ModifierOnBlockBreak;
import name.modid.helpers.modifiers.category.ModifierOnDamage;
import name.modid.helpers.modifiers.category.ModifierOnFirstHit;
import name.modid.helpers.modifiers.category.ModifierOnHit;
import name.modid.helpers.modifiers.category.ModifierOnHitEffect;
import name.modid.helpers.modifiers.category.ModifierOnHitEffectProjectile;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.tooltips.TooltipHelper.Icons;
import name.modid.helpers.modifiers.tooltips.TooltipHelper.InlineIcons;
import name.modid.helpers.modifiers.type.EventType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class TooltipBuilder {
  public enum ModifierCategoryType {
    ATTRIBUTE,
    MULTIPLY_ATTRIBUTE,
    ON_FIRST_HIT,
    ON_DAMAGE,
    ON_BLOCK_BREAK,
    ON_HIT,
    ON_HIT_EFFECT,
    ON_HIT_EFFECT_PROJ,
    AREA_EFFECT,
    CUSTOM_CONDITION,
    UNDEFINED,
  }

  public static final Formatting DEFAULT_TEXT_COLOR = Formatting.GOLD;

  private final GemstoneType gemstoneType;
  private final ModifierItemCategory itemCategory;
  private final GemstoneRarity rarityType;
  private final ModifierCategoryType modifierCategory;
  private final GemstoneModifier modifier;
  private boolean isItemTooltip = false;

  private final Map<ModifierCategoryType, TooltipHandler> handlers = new EnumMap<>(ModifierCategoryType.class);

  public TooltipBuilder(
      GemstoneType gemstoneType,
      ModifierItemCategory itemCategory,
      GemstoneRarity rarityType,
      GemstoneModifier modifier) {
    this.gemstoneType = gemstoneType;
    this.itemCategory = itemCategory;
    this.rarityType = rarityType;
    this.modifier = modifier;
    this.modifierCategory = resolveType(modifier);

    registerHandlers();
  }

  private void registerHandlers() {
    handlers.put(ModifierCategoryType.ATTRIBUTE, new AttributeHandler());
    handlers.put(ModifierCategoryType.MULTIPLY_ATTRIBUTE, new MultiplyAttributeHandler());
    handlers.put(ModifierCategoryType.ON_HIT_EFFECT_PROJ, new OnHitEffectProjHandler());
    handlers.put(ModifierCategoryType.ON_HIT_EFFECT, new OnHitEffectHandler());
    handlers.put(ModifierCategoryType.ON_BLOCK_BREAK, new OnBlockBreakHandler());
    handlers.put(ModifierCategoryType.AREA_EFFECT, new AreaEffectHandler());
    handlers.put(ModifierCategoryType.ON_HIT, new OnHitHandler());
    handlers.put(ModifierCategoryType.ON_FIRST_HIT, new OnFirstHitHandler());
    handlers.put(ModifierCategoryType.CUSTOM_CONDITION, new CustomConditionHandler());
    handlers.put(ModifierCategoryType.UNDEFINED, new UndefinedHandler());
  }

  private ModifierCategoryType resolveType(GemstoneModifier modifier) {
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
    if (modifier instanceof ModifierOnHit)
      return ModifierCategoryType.ON_HIT;
    if (modifier instanceof ModifierOnHitEffect)
      return ModifierCategoryType.ON_HIT_EFFECT;
    if (modifier instanceof ModifierOnHitEffectProjectile)
      return ModifierCategoryType.ON_HIT_EFFECT_PROJ;
    if (modifier instanceof ModifierAreaEffect)
      return ModifierCategoryType.AREA_EFFECT;
    if (modifier instanceof ModifierCustomCondition)
      return ModifierCategoryType.CUSTOM_CONDITION;
    return ModifierCategoryType.UNDEFINED;
  }

  public TooltipBuilder withItemTooltip(boolean flag) {
    this.isItemTooltip = flag;
    return this;
  }

  public MutableText build() {
    String tooltipItemType = isItemTooltip
        ? String.format("tooltip.gemstones.%s_type", itemCategory.toString().toLowerCase())
        : "tooltip.gemstones.without_type";

    MutableText tooltipItemPrefix = Text.translatable(tooltipItemType).formatted(Formatting.GRAY);

    TooltipHandler handler = handlers.getOrDefault(modifierCategory, new UndefinedHandler());
    return tooltipItemPrefix.append(handler.buildTooltip());
  }

  public String formatValue(double value, String postfix) {
    BigDecimal v = BigDecimal.valueOf(value)
        .setScale(2, RoundingMode.HALF_UP)
        .stripTrailingZeros();
    return postfix.isBlank() ? v.toPlainString() : v.toPlainString() + postfix;
  }

  public String getTranslationKey() {
    return String.format("tooltip.gemstones.%s.%s_bonus",
        gemstoneType.toString().toLowerCase(),
        itemCategory.toString().toLowerCase());
  }

  public MutableText getArrowPrefix(boolean isPositive) {
    return Text.literal(isPositive ? InlineIcons.ARROW_UP.getSymbol() : InlineIcons.ARROW_DOWN.getSymbol())
        .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(isPositive ? Formatting.GREEN : Formatting.RED);
  }

  public MutableText getEventText(EventType eventType) {
    MutableText eventIcon = Text.empty();
    Formatting textColor = Formatting.BLUE;

    switch (eventType) {
      case LIGHTNING_BOLT -> eventIcon.append(Text.literal(InlineIcons.LIGHTNING_STRIKE.getSymbol()));
      case EXTRA_HEALTH -> {
        eventIcon.append(Text.literal(InlineIcons.HALF_EXTRA_HEART.getSymbol()));
        textColor = Formatting.YELLOW;
      }
      default -> eventIcon.append(Text.empty());
    }

    eventIcon.styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);

    if (eventIcon.getString() != null) {
      eventIcon.append(Text.literal(" ").styled(style -> style.withFont(Style.DEFAULT_FONT_ID)));
    }

    return Text
        .empty()
        .append(eventIcon)
        .append(Text.literal(eventType.getName()).formatted(textColor));
  }

  private interface TooltipHandler {
    MutableText buildTooltip();
  }

  private class AttributeHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierAttribute m = (ModifierAttribute) modifier;
      double value = m.getLevelValues().get(rarityType);
      boolean isPositive = value > 0;
      double adjustedValue = m.getOperation().equals(Operation.ADD_VALUE) ? value : value * 100;
      String percentString = m.getOperation().equals(Operation.ADD_VALUE) ? "" : "%";
      String formattedValue = formatValue(Math.abs(adjustedValue), percentString);

      MutableText attributeText = Text.empty()
          .append(getArrowPrefix(isPositive))
          .append(Text.literal(formattedValue).formatted(isPositive ? Formatting.GREEN : Formatting.RED));

      return Text.translatable(getTranslationKey(), attributeText).formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class MultiplyAttributeHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierMultiplyAttribute multiplyAttribute = (ModifierMultiplyAttribute) modifier;
      List<MutableText> attributeParts = new ArrayList<>();

      for (ModifierAttribute attr : multiplyAttribute.getInstances()) {
        double value = attr.getLevelValues().get(rarityType);
        boolean isPositive = value > 0;
        double adjustedValue = attr.getOperation().equals(Operation.ADD_VALUE) ? value : value * 100;
        String percentString = attr.getOperation().equals(Operation.ADD_VALUE) ? "" : "%";
        String formattedValue = formatValue(Math.abs(adjustedValue), percentString);

        MutableText attributeText = Text.empty()
            .append(getArrowPrefix(isPositive))
            .append(Text.literal(formattedValue).formatted(isPositive ? Formatting.GREEN : Formatting.RED));

        attributeParts.add(attributeText);
      }

      return Text.translatable(getTranslationKey(), attributeParts.toArray()).formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class OnHitEffectProjHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierOnHitEffectProjectile m = (ModifierOnHitEffectProjectile) modifier;
      double chance = m.getInflitChanceValues().get(rarityType);
      boolean isPositive = chance > 0;
      String formattedChance = formatValue(Math.abs(chance * 100), "%");

      MutableText chanceText = Text.empty()
          .append(getArrowPrefix(isPositive))
          .append(Text.literal(formattedChance).formatted(isPositive ? Formatting.GREEN : Formatting.RED));

      MutableText effectText = Text.empty()
          .append(m.getEffectEntry().value().getName())
          .formatted(m.getEffectEntry().value().isBeneficial() ? Formatting.GREEN : Formatting.RED);

      return Text.translatable(getTranslationKey(), chanceText, effectText).formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class OnHitEffectHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierOnHitEffect m = (ModifierOnHitEffect) modifier;
      double chance = m.getInflitChanceValues().get(rarityType);
      boolean isPositive = chance > 0;
      String formattedChance = formatValue(Math.abs(chance * 100), "%");

      MutableText chanceText = Text.empty()
          .append(getArrowPrefix(isPositive))
          .append(Text.literal(formattedChance).formatted(isPositive ? Formatting.GREEN : Formatting.RED));

      MutableText effectText = Text.empty()
          .append(m.getEffectEntry().value().getName())
          .formatted(m.getEffectEntry().value().isBeneficial() ? Formatting.GREEN : Formatting.RED);

      return Text.translatable(getTranslationKey(), chanceText, effectText).formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class OnBlockBreakHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierOnBlockBreak m = (ModifierOnBlockBreak) modifier;
      double chance = m.getLevelValues().get(rarityType);
      boolean isPositive = chance > 0;
      String formattedChance = formatValue(Math.abs(chance * 100), "%");

      MutableText chanceText = Text.empty()
          .append(getArrowPrefix(isPositive))
          .append(Text.literal(formattedChance).formatted(isPositive ? Formatting.GREEN : Formatting.RED));

      return Text.translatable(getTranslationKey(), chanceText, getEventText(m.getEventType()))
          .formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class AreaEffectHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierAreaEffect m = (ModifierAreaEffect) modifier;
      double radius = m.getRadiusLevels().get(rarityType);
      boolean isPositive = radius > 0;
      String formatted = formatValue(Math.abs(radius), "") + " blocks";

      MutableText chanceText = Text.literal(formatted).formatted(isPositive ? Formatting.GREEN : Formatting.RED);
      MutableText effectText = Text.empty()
          .append(m.getEffectEntry().value().getName())
          .formatted(m.getEffectEntry().value().isBeneficial() ? Formatting.GREEN : Formatting.RED);

      return Text.translatable(getTranslationKey(), effectText, chanceText).formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class OnHitHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierOnHit m = (ModifierOnHit) modifier;
      double chance = m.getEventChances().get(rarityType);
      boolean isPositive = chance > 0;
      String formattedChance = formatValue(Math.abs(chance * 100), "%");

      MutableText chanceText = Text.empty()
          .append(getArrowPrefix(isPositive))
          .append(Text.literal(formattedChance).formatted(isPositive ? Formatting.GREEN : Formatting.RED));

      return Text.translatable(getTranslationKey(), chanceText, getEventText(m.getEventType()))
          .formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class OnFirstHitHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierOnFirstHit m = (ModifierOnFirstHit) modifier;
      double value = m.getValues().get(rarityType);
      boolean isPositive = value > 0;
      String formattedChance = formatValue(Math.abs(value * 100), "%");

      MutableText chanceText = Text.empty()
          .append(getArrowPrefix(isPositive))
          .append(Text.literal(formattedChance).formatted(isPositive ? Formatting.GREEN : Formatting.RED));

      return Text.translatable(getTranslationKey(), chanceText, getEventText(m.getEventType()))
          .formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class CustomConditionHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierCustomCondition m = (ModifierCustomCondition) modifier;
      double value = m.getValues().get(rarityType);
      boolean isPositive = value > 0;
      String postfix = m.getEventType() == EventType.POTION_DURATION ? " seconds" : "";
      String formatted = formatValue(Math.abs(value), postfix);

      MutableText chanceText = Text.empty()
          .append(getArrowPrefix(isPositive))
          .append(Text.literal(formatted).formatted(isPositive ? Formatting.GREEN : Formatting.RED));

      return Text.translatable(getTranslationKey(), chanceText, getEventText(m.getEventType()))
          .formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class UndefinedHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      return Text.literal("Undefined bonus").formatted(Formatting.RED);
    }
  }
}