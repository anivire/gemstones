package name.modid.core.api.tooltips;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierCategoryType;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import name.modid.core.utils.tooltip.BoostedValueFormatter;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TooltipBuilder {
  public static final Formatting DEFAULT_TEXT_COLOR = Formatting.GOLD;
  // Positive values that stack with diminishing returns, plain additive ones stay green
  public static final Formatting DIMINISHING_VALUE_COLOR = Formatting.AQUA;

  private final GemstoneType gemstoneType;
  private final ModifierItemCategory itemCategory;
  private final GemstoneQuality rarityType;
  private final ModifierConfig config;
  private final ModifierConfig baseConfig;
  private final ModifierCategoryType categoryType;

  private final TooltipHandlerRegistry registry;

  private boolean isItemTooltip = false;

  public TooltipBuilder(
      GemstoneType gemstoneType,
      ModifierItemCategory itemCategory,
      GemstoneQuality rarityType,
      GemstoneModifier modifier) {
    this(gemstoneType, itemCategory, rarityType, modifier, null);
  }

  public TooltipBuilder(
      GemstoneType gemstoneType,
      ModifierItemCategory itemCategory,
      GemstoneQuality rarityType,
      GemstoneModifier modifier,
      GemstoneModifier baseModifier) {
    this.gemstoneType = gemstoneType;
    this.itemCategory = itemCategory;
    this.rarityType = rarityType;
    this.config = modifier.getConfig();
    this.baseConfig = baseModifier == null ? null : baseModifier.getConfig();
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
      Map.entry(ModifierConfig.OnFishingConfig.class, ModifierCategoryType.ON_FISHING),
      Map.entry(ModifierConfig.AmplifierConfig.class, ModifierCategoryType.AMPLIFIER));

  private ModifierCategoryType resolveCategory(ModifierConfig config) {
    return CONFIG_TYPE_MAP.getOrDefault(config.getClass(), ModifierCategoryType.UNDEFINED);
  }

  public TooltipBuilder withItemTooltip(boolean flag) {
    this.isItemTooltip = flag;
    return this;
  }

  public MutableText build() {
    MutableText content = registry.get(categoryType).buildTooltip();

    if (!isItemTooltip) {
      return TooltipText.row(TooltipText.socketIcon(gemstoneType), content);
    }

    return TooltipHelper.safeTranslatable("tooltip.gemstones.category_dot")
        .formatted(Formatting.DARK_GRAY)
        .append(TooltipHelper
            .safeTranslatable(String.format("tooltip.gemstones.%s_type", itemCategory.toString().toLowerCase()))
            .formatted(Formatting.GRAY))
        .append(content);
  }

  public ModifierItemCategory getItemCategory() {
    return itemCategory;
  }

  public ModifierConfig getBaseConfig() {
    return baseConfig;
  }

  public String formatValue(double value, String postfix) {
    return BoostedValueFormatter.format(value, postfix);
  }

  public MutableText getArrowPrefix(boolean isPositive) {
    return getArrowPrefix(isPositive, isPositive ? Formatting.GREEN : Formatting.RED);
  }

  public static Formatting getValueColor(boolean isPositive, boolean diminishing) {
    if (!isPositive) {
      return Formatting.RED;
    }
    return diminishing ? DIMINISHING_VALUE_COLOR : Formatting.GREEN;
  }

  // Diminishing bonuses get an arrow with a bar on top: the value grows towards a limit
  public MutableText getValuePrefix(boolean isPositive, boolean diminishing) {
    Formatting color = getValueColor(isPositive, diminishing);
    if (isPositive && diminishing) {
      return TooltipText.icon(InlineIcons.ARROW_UP_DIMINISHING, color);
    }
    return getArrowPrefix(isPositive, color);
  }

  public MutableText getArrowPrefix(boolean isPositive, Formatting formatting) {
    return TooltipText.icon(isPositive ? InlineIcons.ARROW_UP : InlineIcons.ARROW_DOWN, formatting);
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
    return getEventText(eventType, null);
  }

  /**
   * Event name with its icon, e.g. "❤ Extra Health". A non-null {@code amount}
   * goes before the icon in the same color: "2 ❤ Extra Health"
   */
  public MutableText getEventText(EventType eventType, @Nullable String amount) {
    if (eventType == null) {
      return TooltipHelper.safeTranslatable("tooltip.gemstones.event.unknown").formatted(Formatting.RED);
    }

    InlineIcons icon = null;
    Formatting textColor = Formatting.BLUE;

    switch (eventType.getName()) {
      case "ON_BLOCK_BREAK_EXTRA_HEALTH" -> {
        icon = InlineIcons.HALF_EXTRA_HEART;
        textColor = Formatting.YELLOW;
      }
      case "ON_BLOCK_BREAK_HEAL" -> {
        icon = InlineIcons.HALF_HEART;
        textColor = Formatting.GREEN;
      }
      default -> {
      }
    }

    MutableText label = TooltipHelper.safeTranslatable(eventType.getTranslationKey()).formatted(textColor);
    MutableText line = amount == null
        ? TooltipText.line()
        : TooltipText.line(Text.literal(amount + " ").formatted(textColor));

    if (icon != null) {
      line.append(TooltipText.icon(icon)).append(Text.literal(" "));
    }
    return line.append(label);
  }
}
