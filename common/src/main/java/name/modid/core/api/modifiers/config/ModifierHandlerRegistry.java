package name.modid.core.api.modifiers.config;

import java.util.HashMap;
import java.util.Map;

import name.modid.core.api.modifiers.config.handlers.AfterDeathHandler;
import name.modid.core.api.modifiers.config.handlers.BeforeBlockBreakHandler;
import name.modid.core.api.modifiers.config.handlers.BlockBreakHandler;
import name.modid.core.api.modifiers.config.handlers.EffectHandler;
import name.modid.core.api.modifiers.config.handlers.HitMeleeHandler;
import name.modid.core.api.modifiers.config.handlers.HitProjectileHandler;
import name.modid.core.api.modifiers.config.handlers.OnFirstHitHandler;
import name.modid.core.api.modifiers.config.handlers.OnFishingHandler;
import name.modid.core.api.modifiers.config.handlers.OnMobDamageHandler;
import name.modid.core.api.modifiers.config.handlers.OnPlayerDamageHandler;
import name.modid.core.api.modifiers.config.handlers.OnPotionBrewHandler;
import name.modid.core.api.modifiers.config.handlers.PlayerHandler;

public class ModifierHandlerRegistry {
  private static final Map<Class<? extends ModifierConfig>, ModifierHandler<?>> HANDLERS = new HashMap<>();

  static {
    register(ModifierConfig.HitMeleeConfig.class, new HitMeleeHandler());
    register(ModifierConfig.HitProjectileConfig.class, new HitProjectileHandler());
    register(ModifierConfig.HitEffectMeleeConfig.class, new EffectHandler.Melee());
    register(ModifierConfig.HitEffectProjectileConfig.class, new EffectHandler.Projectile());
    register(ModifierConfig.OnFirstHitConfig.class, new OnFirstHitHandler());
    register(ModifierConfig.AreaEffectConfig.class, new EffectHandler.Area());
    register(ModifierConfig.BeforeBlockBreakConfig.class, new BeforeBlockBreakHandler());
    register(ModifierConfig.BlockBreakConfig.class, new BlockBreakHandler());
    register(ModifierConfig.AfterDeathConfig.class, new AfterDeathHandler());
    register(ModifierConfig.PlayerConfig.class, new PlayerHandler());
    register(ModifierConfig.OnPotionBrewConfig.class, new OnPotionBrewHandler());
    register(ModifierConfig.OnMobDamageConfig.class, new OnMobDamageHandler());
    register(ModifierConfig.OnPlayerDamageConfig.class, new OnPlayerDamageHandler());
    register(ModifierConfig.OnFishingConfig.class, new OnFishingHandler());
  }

  private static <T extends ModifierConfig> void register(Class<T> configClass, ModifierHandler<T> handler) {
    HANDLERS.put(configClass, handler);
  }

  @SuppressWarnings("unchecked")
  public static <T extends ModifierConfig> ModifierHandler<T> getHandler(T config) {
    return (ModifierHandler<T>) HANDLERS.get(config.getClass());
  }
}