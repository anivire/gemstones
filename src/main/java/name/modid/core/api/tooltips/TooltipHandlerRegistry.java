package name.modid.core.api.tooltips;

import java.util.EnumMap;
import java.util.Map;

import name.modid.core.api.modifiers.config.ModifierCategoryType;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.tooltips.handlers.AreaEffectHandler;
import name.modid.core.api.tooltips.handlers.AttributeHandler;
import name.modid.core.api.tooltips.handlers.DefaultPercentHandler;
import name.modid.core.api.tooltips.handlers.MultiplyAttributeHandler;
import name.modid.core.api.tooltips.handlers.OnBlockBreakHandler;
import name.modid.core.api.tooltips.handlers.OnHitEffectHandler;
import name.modid.core.api.tooltips.handlers.OnHitHandler;
import name.modid.core.api.tooltips.handlers.OnPlayerDamageHandler;
import name.modid.core.api.tooltips.handlers.OnPotionBrewHandler;
import name.modid.core.api.tooltips.handlers.PlayerHandler;
import name.modid.core.api.tooltips.handlers.TooltipHandler;
import name.modid.core.api.tooltips.handlers.UndefinedHandler;

public final class TooltipHandlerRegistry {
  private final Map<ModifierCategoryType, TooltipHandler> handlers = new EnumMap<>(ModifierCategoryType.class);

  public TooltipHandlerRegistry(
      TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    register(builder, config, rarityType);
  }

  private void register(
      TooltipBuilder builder, ModifierConfig config, GemstoneQuality rarityType) {
    // Default event handlers
    handlers.put(ModifierCategoryType.ON_FISHING,
        new DefaultPercentHandler<>(builder, config, rarityType));
    handlers.put(ModifierCategoryType.ON_DEATH,
        new DefaultPercentHandler<>(builder, config, rarityType));
    handlers.put(ModifierCategoryType.ON_BEFORE_BLOCK_BREAK,
        new DefaultPercentHandler<>(builder, config, rarityType));
    handlers.put(ModifierCategoryType.ON_MOB_DAMAGE,
        new DefaultPercentHandler<>(builder, config, rarityType));
    handlers.put(ModifierCategoryType.ON_FIRST_HIT,
        new DefaultPercentHandler<>(builder, config, rarityType));

    // Custom event handlers
    handlers.put(ModifierCategoryType.ON_PLAYER_DAMAGE,
        new OnPlayerDamageHandler(builder, config, rarityType));
    handlers.put(ModifierCategoryType.PLAYER,
        new PlayerHandler(builder, config, rarityType));
    handlers.put(ModifierCategoryType.ON_POTION_BREW,
        new OnPotionBrewHandler(builder, config, rarityType));
    handlers.put(ModifierCategoryType.ON_BLOCK_BREAK,
        new OnBlockBreakHandler(builder, config, rarityType));
    handlers.put(ModifierCategoryType.ON_HIT_MELEE,
        new OnHitHandler<>(builder, config, rarityType, false));
    handlers.put(ModifierCategoryType.ON_HIT_PROJECTILE,
        new OnHitHandler<>(builder, config, rarityType, true));

    // Attribute handlers
    handlers.put(ModifierCategoryType.ATTRIBUTE,
        new AttributeHandler(builder, config, rarityType));
    handlers.put(ModifierCategoryType.MULTIPLY_ATTRIBUTE,
        new MultiplyAttributeHandler(builder, config, rarityType));

    // Effect handlers
    handlers.put(ModifierCategoryType.ON_HIT_EFFECT_MELEE,
        new OnHitEffectHandler<>(builder, config, rarityType, false));
    handlers.put(ModifierCategoryType.ON_HIT_EFFECT_PROJECTILE,
        new OnHitEffectHandler<>(builder, config, rarityType, true));
    handlers.put(ModifierCategoryType.AREA_EFFECT,
        new AreaEffectHandler(builder, config, rarityType));

    handlers.put(ModifierCategoryType.UNDEFINED, new UndefinedHandler());
  }

  public TooltipHandler get(ModifierCategoryType type) {
    return handlers.getOrDefault(type, new UndefinedHandler());
  }
}