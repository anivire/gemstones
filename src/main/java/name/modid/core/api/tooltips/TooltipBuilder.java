package name.modid.core.api.tooltips;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierCategoryType;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.core.api.tooltips.TooltipHelper.Icons;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
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
  private final ModifierCategoryType categoryType;

  private final TooltipHandlerRegistry registry;

  private boolean isItemTooltip = false;

  public TooltipBuilder(
      GemstoneType gemstoneType,
      ModifierItemCategory itemCategory,
      GemstoneQuality rarityType,
      GemstoneModifier modifier) {
    this.gemstoneType = gemstoneType;
    this.itemCategory = itemCategory;
    this.rarityType = rarityType;
    this.config = modifier.getConfig();
    this.categoryType = resolveCategory(config);
    this.registry = new TooltipHandlerRegistry(this, config, rarityType);
  }

  private static final Map<Class<? extends ModifierConfig>, ModifierCategoryType> CONFIG_TYPE_MAP = Map.ofEntries(
      Map.entry(ModifierConfig.AttributeConfig.class, ModifierCategoryType.ATTRIBUTE),
      Map.entry(ModifierConfig.MultiplyAttributeConfig.class, ModifierCategoryType.MULTIPLY_ATTRIBUTE),
      Map.entry(ModifierConfig.HitMeleeConfig.class, ModifierCategoryType.ON_HIT_MELEE),
      Map.entry(ModifierConfig.HitProjectileConfig.class, ModifierCategoryType.ON_HIT_PROJECTILE),
      Map.entry(ModifierConfig.HitEffectMeleeConfig.class, ModifierCategoryType.ON_HIT_EFFECT_MELEE),
      Map.entry(ModifierConfig.HitEffectProjectileConfig.class, ModifierCategoryType.ON_HIT_EFFECT_PROJECTILE),
      Map.entry(ModifierConfig.AreaEffectConfig.class, ModifierCategoryType.AREA_EFFECT),
      Map.entry(ModifierConfig.OnMobDamageConfig.class, ModifierCategoryType.ON_MOB_DAMAGE),
      Map.entry(ModifierConfig.OnPlayerDamageConfig.class, ModifierCategoryType.ON_PLAYER_DAMAGE),
      Map.entry(ModifierConfig.OnFirstHitConfig.class, ModifierCategoryType.ON_FIRST_HIT),
      Map.entry(ModifierConfig.BlockBreakConfig.class, ModifierCategoryType.ON_BLOCK_BREAK),
      Map.entry(ModifierConfig.BeforeBlockBreakConfig.class, ModifierCategoryType.ON_BEFORE_BLOCK_BREAK),
      Map.entry(ModifierConfig.PlayerConfig.class, ModifierCategoryType.PLAYER),
      Map.entry(ModifierConfig.AfterDeathConfig.class, ModifierCategoryType.ON_DEATH),
      Map.entry(ModifierConfig.OnPotionBrewConfig.class, ModifierCategoryType.ON_POTION_BREW),
      Map.entry(ModifierConfig.OnFishingConfig.class, ModifierCategoryType.ON_FISHING));

  private ModifierCategoryType resolveCategory(ModifierConfig config) {
    return CONFIG_TYPE_MAP.getOrDefault(config.getClass(), ModifierCategoryType.UNDEFINED);
  }

  public TooltipBuilder withItemTooltip(boolean flag) {
    this.isItemTooltip = flag;
    return this;
  }

  public MutableText build() {
    MutableText prefix;

    if (!isItemTooltip) {
      String symbol = gemstoneType == GemstoneType.EMPTY
          ? InlineIcons.EMPTY.getSymbol()
          : gemstoneType == GemstoneType.LOCKED
              ? InlineIcons.LOCKED.getSymbol()
              : gemstoneType.getGemstoneLiteral();

      String fontPath = (gemstoneType != GemstoneType.EMPTY && gemstoneType != GemstoneType.LOCKED)
          ? Icons.INLINE_GEMSTONE.getPath()
          : Icons.INLINE.getPath();

      prefix = Text.literal(symbol)
          .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, fontPath)))
          .formatted(Formatting.WHITE)
          .append(Text.literal(" > ").formatted(Formatting.DARK_GRAY)
              .styled(style -> style.withFont(Style.DEFAULT_FONT_ID)));
    } else {
      prefix = TooltipHelper.safeTranslatable("tooltip.gemstones.category_dot")
          .formatted(Formatting.DARK_GRAY)
          .append(TooltipHelper
              .safeTranslatable(String.format("tooltip.gemstones.%s_type", itemCategory.toString().toLowerCase()))
              .formatted(Formatting.GRAY));
    }

    return prefix.append(
        registry.get(categoryType)
            .buildTooltip()
            .styled(style -> style.withFont(Style.DEFAULT_FONT_ID)));
  }

  public String formatValue(double value, String postfix) {
    BigDecimal v = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
    return postfix.isBlank() ? v.toPlainString() : v.toPlainString() + postfix;
  }

  public MutableText getArrowPrefix(boolean isPositive) {
    return Text.literal(isPositive ? InlineIcons.ARROW_UP.getSymbol() : InlineIcons.ARROW_DOWN.getSymbol())
        .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(isPositive ? Formatting.GREEN : Formatting.RED);
  }

  public String getTranslationKeyByModifier(ModifierCategoryType category) {
    return category == null ? "tooltip.gemstones.category.unknown"
        : "tooltip.gemstones." + category.toString().toLowerCase();
  }

  public String getTranslationKeyByEvent(EventType event) {
    return event == null ? "tooltip.gemstones.event.unknown"
        : "tooltip.gemstones.event." + event.getName().toLowerCase();
  }

  public MutableText getEventText(Object eventType) {
    return Text.translatable("tooltip.gemstones.event_text." + eventType.toString().toLowerCase());
  }

  public MutableText getEventText(EventType eventType) {
    MutableText eventIcon = Text.empty();
    Formatting textColor = Formatting.BLUE;

    if (eventType == null) {
      return TooltipHelper.safeTranslatable("tooltip.gemstones.event.unknown").formatted(Formatting.RED);
    }

    switch (eventType.getName()) {
      case "ON_BLOCK_BREAK_EXTRA_HEALTH" -> {
        eventIcon.append(Text.literal(InlineIcons.HALF_EXTRA_HEART.getSymbol()));
        textColor = Formatting.YELLOW;
      }
      case "ON_BLOCK_BREAK_HEAL" -> {
        eventIcon.append(Text.literal(InlineIcons.HALF_HEART.getSymbol()));
        textColor = Formatting.GREEN;
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
        .append(TooltipHelper.safeTranslatable(eventType.getTranslationKey()).formatted(textColor));
  }
}