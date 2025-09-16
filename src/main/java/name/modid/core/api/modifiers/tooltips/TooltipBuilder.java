package name.modid.core.api.modifiers.tooltips;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.tooltips.TooltipHelper.Icons;
import name.modid.core.api.modifiers.tooltips.TooltipHelper.InlineIcons;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
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
    ON_HIT_MELEE,
    ON_HIT_PROJECTILE,
    ON_HIT_EFFECT,
    ON_HIT_EFFECT_PROJECTILE,
    AREA_EFFECT,
    CUSTOM_CONDITION,
    UNDEFINED,
  }

  public static final Formatting DEFAULT_TEXT_COLOR = Formatting.GOLD;

  private final GemstoneType gemstoneType;
  private final ModifierItemCategory itemCategory;
  private final GemstoneQuality rarityType;
  private final ModifierConfig config;
  private final ModifierCategoryType modifierCategory;
  private final GemstoneModifier modifier;
  private boolean isItemTooltip = false;

  private final Map<ModifierCategoryType, TooltipHandler> handlers = new EnumMap<>(ModifierCategoryType.class);

  public TooltipBuilder(
      GemstoneType gemstoneType,
      ModifierItemCategory itemCategory,
      GemstoneQuality rarityType,
      GemstoneModifier modifier) {
    this.gemstoneType = gemstoneType;
    this.itemCategory = itemCategory;
    this.rarityType = rarityType;
    this.modifier = modifier;
    this.config = modifier.getConfig();
    this.modifierCategory = resolveType(config);

    registerHandlers();
  }

  private void registerHandlers() {
    handlers.put(ModifierCategoryType.ATTRIBUTE, new AttributeHandler());
    handlers.put(ModifierCategoryType.MULTIPLY_ATTRIBUTE, new MultiplyAttributeHandler());
    handlers.put(ModifierCategoryType.ON_HIT_EFFECT_PROJECTILE, new OnHitEffectProjHandler());
    handlers.put(ModifierCategoryType.ON_HIT_EFFECT, new OnHitEffectHandler());
    handlers.put(ModifierCategoryType.ON_BLOCK_BREAK, new OnBlockBreakHandler());
    handlers.put(ModifierCategoryType.AREA_EFFECT, new AreaEffectHandler());
    handlers.put(ModifierCategoryType.ON_HIT_PROJECTILE, new OnHitProjectileHandler());
    handlers.put(ModifierCategoryType.ON_HIT_MELEE, new OnHitMeleeHandler());
    handlers.put(ModifierCategoryType.ON_FIRST_HIT, new OnFirstHitHandler());
    handlers.put(ModifierCategoryType.CUSTOM_CONDITION, new CustomConditionHandler());
    handlers.put(ModifierCategoryType.UNDEFINED, new UndefinedHandler());
  }

  private ModifierCategoryType resolveType(ModifierConfig config) {
    if (config instanceof ModifierConfig.AttributeConfig)
      return ModifierCategoryType.ATTRIBUTE;
    if (config instanceof ModifierConfig.MultiplyAttributeConfig)
      return ModifierCategoryType.MULTIPLY_ATTRIBUTE;
    if (config instanceof ModifierConfig.HitMeleeConfig)
      return ModifierCategoryType.ON_HIT_MELEE;
    if (config instanceof ModifierConfig.HitProjectileConfig)
      return ModifierCategoryType.ON_HIT_PROJECTILE;
    if (config instanceof ModifierConfig.HitEffectMeleeConfig)
      return ModifierCategoryType.ON_HIT_EFFECT;
    if (config instanceof ModifierConfig.HitEffectProjectileConfig)
      return ModifierCategoryType.ON_HIT_EFFECT_PROJECTILE;
    if (config instanceof ModifierConfig.AreaEffectConfig)
      return ModifierCategoryType.AREA_EFFECT;
    if (config instanceof ModifierConfig.DamageConfig)
      return ModifierCategoryType.ON_DAMAGE;
    if (config instanceof ModifierConfig.OnFirstHitConfig)
      return ModifierCategoryType.ON_FIRST_HIT;
    if (config instanceof ModifierConfig.BlockBreakConfig)
      return ModifierCategoryType.ON_BLOCK_BREAK;
    if (config instanceof ModifierConfig.CustomConditionConfig)
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

    TooltipHandler handler = handlers.getOrDefault(modifierCategory, new UndefinedHandler());

    if (Gemstones.ALT_STYLE) {
      MutableText prefix;

      if (!isItemTooltip) {
        prefix = Text.literal(gemstoneType.getGemstoneLiteral())
            .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE_GEMSTONE.getPath())))
            .formatted(Formatting.WHITE).append(Text.literal(" > ").formatted(Formatting.DARK_GRAY)
                .styled(style -> style.withFont(Style.DEFAULT_FONT_ID)));
      } else {
        prefix = Text.translatable(tooltipItemType).formatted(Formatting.DARK_GRAY);
      }

      return prefix.append(handler.buildTooltip().styled(style -> style.withFont(Style.DEFAULT_FONT_ID)));
    } else {
      MutableText tooltipItemPrefix = Text.translatable(tooltipItemType).formatted(Formatting.DARK_GRAY);
      return tooltipItemPrefix.append(handler.buildTooltip());
    }
  }

  public String formatValue(double value, String postfix) {
    BigDecimal v = BigDecimal.valueOf(value)
        .setScale(2, RoundingMode.HALF_UP)
        .stripTrailingZeros();
    return postfix.isBlank() ? v.toPlainString() : v.toPlainString() + postfix;
  }

  public String getTranslationKeyByModifier(ModifierCategoryType category) {
    return String.format("tooltip.gemstones.%s", category.toString());
  }

  public String getTranslationKeyByEvent(EventType event) {
    return String.format("tooltip.gemstones.EVENT.%s", event.toString());
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
      case EXTRA_HEALTH -> {
        eventIcon.append(Text.literal(InlineIcons.HALF_EXTRA_HEART.getSymbol()));
        textColor = Formatting.YELLOW;
      }
      case HEAL -> {
        eventIcon.append(Text.literal(InlineIcons.HALF_HEART.getSymbol()));
        textColor = Formatting.RED;
      }
      default -> eventIcon.append(Text.empty());
    }

    eventIcon.styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);

    if (!eventIcon.getString().isBlank()) {
      eventIcon.append(Text.literal(" ").styled(style -> style.withFont(Style.DEFAULT_FONT_ID)));
    }

    return Text.empty().append(eventIcon)
        .append(Text.translatable(eventType.getTranslationKey()).formatted(textColor));
  }

  private interface TooltipHandler {
    MutableText buildTooltip();
  }

  private class AttributeHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierConfig.AttributeConfig cfg = (ModifierConfig.AttributeConfig) config;
      double value = cfg.values().get(rarityType);
      boolean isPositive = value > 0;
      double adjustedValue = cfg.operation().equals(Operation.ADD_VALUE) ? value : value * 100;
      String percentString = cfg.operation().equals(Operation.ADD_VALUE) ? "" : "%";
      String formattedValue = formatValue(Math.abs(adjustedValue), percentString);

      MutableText attrText = Text.empty()
          .append(getArrowPrefix(isPositive))
          .append(Text.literal(formattedValue).formatted(isPositive ? Formatting.GREEN : Formatting.RED));

      return Text.translatable(getTranslationKeyByModifier(ModifierCategoryType.ATTRIBUTE),
          attrText,
          Text.translatable(cfg.attribute().value().getTranslationKey().toLowerCase()))
          .formatted(Formatting.BLUE);
    }
  }

  private class MultiplyAttributeHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierConfig.MultiplyAttributeConfig cfg = (ModifierConfig.MultiplyAttributeConfig) config;

      MutableText combined = Text.empty();
      for (ModifierConfig.AttributeConfig sub : cfg.instances()) {
        double value = sub.values().get(rarityType);
        boolean isPositive = value > 0;
        double adjustedValue = sub.operation().equals(Operation.ADD_VALUE) ? value : value * 100;
        String percentString = sub.operation().equals(Operation.ADD_VALUE) ? "" : "%";
        String formattedValue = formatValue(Math.abs(adjustedValue), percentString);

        combined.append(getArrowPrefix(isPositive))
            .append(Text.literal(formattedValue).formatted(isPositive ? Formatting.GREEN : Formatting.RED))
            .append(" ")
            .append(Text.translatable(sub.attribute().value().getTranslationKey().toLowerCase()))
            .append(" ");
      }

      return Text.translatable(getTranslationKeyByModifier(ModifierCategoryType.MULTIPLY_ATTRIBUTE), combined)
          .formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class OnHitEffectProjHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierConfig.HitEffectProjectileConfig cfg = (ModifierConfig.HitEffectProjectileConfig) config;
      double chance = cfg.chance().get(rarityType);
      boolean isPositive = chance > 0;
      String formattedChance = formatValue(Math.abs(chance * 100), "%");

      MutableText effectText = Text.empty().append(cfg.effect().value().getName())
          .formatted(cfg.effect().value().isBeneficial() ? Formatting.GREEN : Formatting.RED);

      return Text.translatable(getTranslationKeyByModifier(ModifierCategoryType.ON_HIT_EFFECT_PROJECTILE),
          getArrowPrefix(isPositive).append(Text.literal(formattedChance)
              .formatted(isPositive ? Formatting.GREEN : Formatting.RED)),
          effectText).formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class OnHitEffectHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierConfig.HitEffectMeleeConfig cfg = (ModifierConfig.HitEffectMeleeConfig) config;
      double chance = cfg.chance().get(rarityType);
      boolean isPositive = chance > 0;
      String formattedChance = formatValue(Math.abs(chance * 100), "%");

      MutableText effectText = Text.empty().append(cfg.effect().value().getName())
          .formatted(cfg.effect().value().isBeneficial() ? Formatting.GREEN : Formatting.RED);

      return Text.translatable(getTranslationKeyByModifier(ModifierCategoryType.ON_HIT_EFFECT),
          getArrowPrefix(isPositive).append(Text.literal(formattedChance)
              .formatted(isPositive ? Formatting.GREEN : Formatting.RED)),
          effectText).formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class OnBlockBreakHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierConfig.BlockBreakConfig cfg = (ModifierConfig.BlockBreakConfig) config;
      double chance = cfg.values().get(rarityType);
      boolean isPositive = chance > 0;
      String formattedChance = formatValue(Math.abs(chance * 100), "%");

      return Text.translatable(getTranslationKeyByEvent(cfg.eventType()),
          getArrowPrefix(isPositive).append(Text.literal(formattedChance)
              .formatted(isPositive ? Formatting.GREEN : Formatting.RED)),
          getEventText(cfg.eventType()))
          .formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class AreaEffectHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierConfig.AreaEffectConfig cfg = (ModifierConfig.AreaEffectConfig) config;
      double radius = cfg.radiusLevels().get(rarityType);
      boolean isPositive = radius > 0;
      String formatted = formatValue(Math.abs(radius), "") + " blocks";

      MutableText chanceText = Text.literal(formatted).formatted(isPositive ? Formatting.GREEN : Formatting.RED);
      MutableText effectText = Text.empty()
          .append(cfg.effect().value().getName())
          .formatted(cfg.effect().value().isBeneficial() ? Formatting.GREEN : Formatting.RED);

      return Text.translatable(getTranslationKeyByModifier(ModifierCategoryType.AREA_EFFECT), effectText, chanceText)
          .formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class OnHitProjectileHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierConfig.HitProjectileConfig cfg = (ModifierConfig.HitProjectileConfig) config;
      double chance = cfg.chance().get(rarityType);
      boolean isPositive = chance > 0;
      String formattedChance = formatValue(Math.abs(chance * 100), "%");

      return Text.translatable(getTranslationKeyByEvent(cfg.eventType()),
          getArrowPrefix(isPositive).append(Text.literal(formattedChance)
              .formatted(isPositive ? Formatting.GREEN : Formatting.RED)),
          getEventText(cfg.eventType())).formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class OnHitMeleeHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierConfig.HitMeleeConfig cfg = (ModifierConfig.HitMeleeConfig) config;
      double chance = cfg.chance().get(rarityType);
      boolean isPositive = chance > 0;
      String formattedChance = formatValue(Math.abs(chance * 100), "%");

      return Text.translatable(getTranslationKeyByEvent(cfg.eventType()),
          getArrowPrefix(isPositive).append(Text.literal(formattedChance)
              .formatted(isPositive ? Formatting.GREEN : Formatting.RED)),
          getEventText(cfg.eventType())).formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class OnFirstHitHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierConfig.OnFirstHitConfig cfg = (ModifierConfig.OnFirstHitConfig) config;
      double value = cfg.values().get(rarityType);
      boolean isPositive = value > 0;
      String formatted = formatValue(Math.abs(value * 100), "%");

      return Text.translatable(getTranslationKeyByEvent(cfg.eventType()),
          getArrowPrefix(isPositive).append(Text.literal(formatted)
              .formatted(isPositive ? Formatting.GREEN : Formatting.RED)),
          getEventText(cfg.eventType())).formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class CustomConditionHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      ModifierConfig.CustomConditionConfig cfg = (ModifierConfig.CustomConditionConfig) config;
      double value = cfg.value().get(rarityType);
      boolean isPositive = value > 0;
      String postfix = cfg.eventType() == EventType.POTION_DURATION ? " seconds"
          : (cfg.eventType() == EventType.INCREASE_MOSSY_BOX_DROP || cfg.eventType() == EventType.INCREASE_MOB_SPAWNRATE
              ? "%"
              : "");
      String formatted = formatValue(
          Math.abs(value) * (cfg.eventType() == EventType.INCREASE_MOSSY_BOX_DROP
              || cfg.eventType() == EventType.INCREASE_MOB_SPAWNRATE ? 100 : 1),
          postfix);

      return Text.translatable(getTranslationKeyByEvent(cfg.eventType()),
          getArrowPrefix(isPositive).append(Text.literal(formatted)
              .formatted(isPositive ? Formatting.GREEN : Formatting.RED)),
          getEventText(cfg.eventType())).formatted(DEFAULT_TEXT_COLOR);
    }
  }

  private class UndefinedHandler implements TooltipHandler {
    @Override
    public MutableText buildTooltip() {
      return Text.literal("Undefined bonus").formatted(Formatting.RED);
    }
  }
}