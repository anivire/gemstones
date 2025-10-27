package name.modid.core.api.modifiers.tooltips;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierCategoryType;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.tooltips.TooltipHelper.Icons;
import name.modid.core.api.modifiers.tooltips.TooltipHelper.InlineIcons;
import name.modid.core.api.modifiers.tooltips.handlers.AfterDeathHandler;
import name.modid.core.api.modifiers.tooltips.handlers.AreaEffectHandler;
import name.modid.core.api.modifiers.tooltips.handlers.AttributeHandler;
import name.modid.core.api.modifiers.tooltips.handlers.MultiplyAttributeHandler;
import name.modid.core.api.modifiers.tooltips.handlers.OnBlockBreakHandler;
import name.modid.core.api.modifiers.tooltips.handlers.OnFirstHitHandler;
import name.modid.core.api.modifiers.tooltips.handlers.OnHitEffectHandler;
import name.modid.core.api.modifiers.tooltips.handlers.OnHitHandler;
import name.modid.core.api.modifiers.tooltips.handlers.OnPotionBrewHandler;
import name.modid.core.api.modifiers.tooltips.handlers.TooltipHandler;
import name.modid.core.api.modifiers.tooltips.handlers.UndefinedHandler;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class TooltipBuilder {
  public static final Formatting DEFAULT_TEXT_COLOR = Formatting.GOLD;

  private final GemstoneType gemstoneType;
  private final ModifierItemCategory itemCategory;
  private final GemstoneQuality rarityType;
  private final ModifierConfig config;
  private final ModifierCategoryType modifierCategory;
  private boolean isItemTooltip = false;

  private final Map<ModifierCategoryType, TooltipHandler> handlers = new EnumMap<>(ModifierCategoryType.class);

  public TooltipBuilder(GemstoneType gemstoneType,
      ModifierItemCategory itemCategory,
      GemstoneQuality rarityType,
      GemstoneModifier modifier) {
    this.gemstoneType = gemstoneType;
    this.itemCategory = itemCategory;
    this.rarityType = rarityType;
    this.config = modifier.getConfig();
    this.modifierCategory = resolveType(config);

    registerHandlers();
  }

  private void registerHandlers() {
    handlers.put(ModifierCategoryType.ATTRIBUTE, new AttributeHandler(this, config, rarityType));
    handlers.put(ModifierCategoryType.MULTIPLY_ATTRIBUTE, new MultiplyAttributeHandler(this, config, rarityType));
    handlers.put(ModifierCategoryType.ON_HIT_MELEE, new OnHitHandler<>(this, config, rarityType, false));
    handlers.put(ModifierCategoryType.ON_HIT_PROJECTILE, new OnHitHandler<>(this, config, rarityType, true));
    handlers.put(ModifierCategoryType.ON_HIT_EFFECT_MELEE, new OnHitEffectHandler<>(this, config, rarityType, false));
    handlers.put(ModifierCategoryType.ON_HIT_EFFECT_PROJECTILE,
        new OnHitEffectHandler<>(this, config, rarityType, true));
    handlers.put(ModifierCategoryType.ON_BLOCK_BREAK, new OnBlockBreakHandler(this, config, rarityType));
    handlers.put(ModifierCategoryType.AREA_EFFECT, new AreaEffectHandler(this, config, rarityType));
    handlers.put(ModifierCategoryType.ON_FIRST_HIT, new OnFirstHitHandler(this, config, rarityType));
    // handlers.put(ModifierCategoryType.PLAYER, new PlayerHandler(this, config,
    // rarityType));
    handlers.put(ModifierCategoryType.ON_DEATH, new AfterDeathHandler(this, config, rarityType));
    handlers.put(ModifierCategoryType.ON_POTION_BREW, new OnPotionBrewHandler(this, config, rarityType));
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
      return ModifierCategoryType.ON_HIT_EFFECT_MELEE;
    if (config instanceof ModifierConfig.HitEffectProjectileConfig)
      return ModifierCategoryType.ON_HIT_EFFECT_PROJECTILE;
    if (config instanceof ModifierConfig.AreaEffectConfig)
      return ModifierCategoryType.AREA_EFFECT;
    if (config instanceof ModifierConfig.OnDamageConfig)
      return ModifierCategoryType.ON_DAMAGE;
    if (config instanceof ModifierConfig.OnFirstHitConfig)
      return ModifierCategoryType.ON_FIRST_HIT;
    if (config instanceof ModifierConfig.BlockBreakConfig)
      return ModifierCategoryType.ON_BLOCK_BREAK;
    if (config instanceof ModifierConfig.PlayerConfig)
      return ModifierCategoryType.PLAYER;
    if (config instanceof ModifierConfig.OnPotionBrewConfig)
      return ModifierCategoryType.ON_POTION_BREW;
    return ModifierCategoryType.UNDEFINED;
  }

  public TooltipBuilder withItemTooltip(boolean flag) {
    this.isItemTooltip = flag;
    return this;
  }

  public MutableText build() {
    String tooltipItemType = isItemTooltip
        ? "tooltip.gemstones." + itemCategory.toString().toLowerCase() + "_type"
        : "tooltip.gemstones.category_dot";

    TooltipHandler handler = handlers.getOrDefault(modifierCategory, new UndefinedHandler());

    if (Gemstones.ALT_STYLE) {
      MutableText prefix;

      if (!isItemTooltip) {
        String l = gemstoneType == GemstoneType.EMPTY
            ? InlineIcons.EMPTY.getSymbol()
            : gemstoneType == GemstoneType.LOCKED
                ? InlineIcons.LOCKED.getSymbol()
                : gemstoneType.getGemstoneLiteral();

        String p = gemstoneType != GemstoneType.EMPTY && gemstoneType != GemstoneType.LOCKED
            ? Icons.INLINE_GEMSTONE.getPath()
            : Icons.INLINE.getPath();

        prefix = Text.literal(l)
            .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, p)))
            .formatted(Formatting.WHITE)
            .append(Text.literal(" > ").formatted(Formatting.DARK_GRAY)
                .styled(style -> style.withFont(Style.DEFAULT_FONT_ID)));

      } else {
        prefix = Text.translatable("tooltip.gemstones.category_dot").formatted(Formatting.DARK_GRAY)
            .append(Text.translatable(tooltipItemType).formatted(Formatting.GRAY));
      }

      return prefix.append(handler.buildTooltip()
          .styled(style -> style.withFont(Style.DEFAULT_FONT_ID)));
    } else {
      MutableText prefix = Text.translatable(tooltipItemType).formatted(Formatting.DARK_GRAY);
      return prefix.append(handler.buildTooltip());
    }
  }

  public String formatValue(double value, String postfix) {
    BigDecimal v = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
    return postfix.isBlank() ? v.toPlainString() : v.toPlainString() + postfix;
  }

  public String getTranslationKeyByModifier(ModifierCategoryType category) {
    return category == null ? "tooltip.gemstones.category.unknown"
        : "tooltip.gemstones." + category.toString().toLowerCase();
  }

  public String getTranslationKeyByEvent(EventType event) {
    return event == null ? "tooltip.gemstones.event.unknown"
        : "tooltip.gemstones.event." + event.toString().toLowerCase();
  }

  public MutableText getArrowPrefix(boolean isPositive) {
    return Text.literal(isPositive ? InlineIcons.ARROW_UP.getSymbol() : InlineIcons.ARROW_DOWN.getSymbol())
        .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(isPositive ? Formatting.GREEN : Formatting.RED);
  }

  public MutableText getEventText(EventType eventType) {
    MutableText eventIcon = Text.empty();
    Formatting textColor = Formatting.BLUE;

    if (eventType == null) {
      return Text.translatable("tooltip.gemstones.event.unknown").formatted(Formatting.RED);
    }

    switch (eventType) {
      case ON_BLOCK_BREAK_EXTRA_HEALTH -> {
        eventIcon.append(Text.literal(InlineIcons.HALF_EXTRA_HEART.getSymbol()));
        textColor = Formatting.YELLOW;
      }
      case ON_BLOCK_BREAK_HEAL -> {
        eventIcon.append(Text.literal(InlineIcons.HALF_HEART.getSymbol()));
        textColor = Formatting.RED;
      }
      default -> {
      }
    }

    eventIcon.styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);

    if (!eventIcon.getString().isBlank()) {
      eventIcon.append(Text.literal(" ").styled(style -> style.withFont(Style.DEFAULT_FONT_ID)));
    }

    return Text.empty().append(eventIcon)
        .append(Text.translatable(eventType.getTranslationKey()).formatted(textColor));
  }
}