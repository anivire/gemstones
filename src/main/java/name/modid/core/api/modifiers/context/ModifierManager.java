package name.modid.core.api.modifiers.context;

import java.util.List;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.config.Modifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import net.minecraft.world.World;

public class ModifierManager {

  public static void applyModifiers(List<Modifier> modifiers, ModifierContext ctx) {
    for (Modifier modifier : modifiers) {
      ModifierConfig cfg = modifier.getConfig();
      GemstoneQuality rarity = modifier.getRarityType();

      switch (cfg) {
        case ModifierConfig.AttributeConfig attr -> {
          // TODO: в будущем добавить применение атрибутов
        }
        case ModifierConfig.HitMeleeConfig hit -> {
          double chance = hit.chance().get(rarity);
          if (proc(ctx.world(), chance)) {
            ModifierHandler.handleMeleeEvent(hit.eventType(), ctx);
          }
        }
        case ModifierConfig.HitProjectileConfig hitProj -> {
          double chance = hitProj.chance().get(rarity);
          if (proc(ctx.world(), chance)) {
            ModifierHandler.handleProjectileEvent(hitProj.eventType(), ctx);
          }
        }
        case ModifierConfig.HitEffectMeleeConfig effect -> {
          double chance = effect.chance().get(rarity);
          if (proc(ctx.world(), chance)) {
            ModifierHandler.applyStatusEffect(effect, ctx.target(), ctx.world());
          }
        }
        case ModifierConfig.HitEffectProjectileConfig effectProj -> {
          double chance = effectProj.chance().get(rarity);
          if (proc(ctx.world(), chance)) {
            ModifierHandler.applyStatusEffect(effectProj, ctx.target(), ctx.world());
          }
        }
        case ModifierConfig.BlockBreakConfig blockCfg -> {
          double chance = blockCfg.values().get(rarity);
          if (proc(ctx.world(), chance)) {
            ModifierHandler.handleBlockBreak(blockCfg.eventType(), ctx);
          }
        }
        case ModifierConfig.DamageConfig dmg -> {
          double chance = dmg.values().get(rarity);
          if (proc(ctx.world(), chance)) {
            ModifierHandler.handleDamage(dmg, ctx);
          }
        }
        case ModifierConfig.AreaEffectConfig area -> {
          double radius = area.radiusLevels().get(rarity);
          ModifierHandler.handleAreaEffect(area, ctx, radius);
        }
        case ModifierConfig.CustomConditionConfig cond -> {
          double chance = cond.value().get(rarity);
          if (proc(ctx.world(), chance)) {
            ModifierHandler.handleCustom(cond.eventType(), cond, ctx);
          }
        }
      }
    }
  }

  private static boolean proc(World world, double chance) {
    return world.getRandom().nextDouble() < chance;
  }
}