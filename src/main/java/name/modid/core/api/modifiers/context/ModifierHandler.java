package name.modid.core.api.modifiers.context;

import name.modid.core.api.modifiers.EventType;
import name.modid.core.api.modifiers.config.ModifierConfig.AreaEffectConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.CustomConditionConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.DamageConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectMeleeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectProjectileConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class ModifierHandler {
  public static void handleCustom(EventType eventType, CustomConditionConfig cond, ModifierContext ctx) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleCustom'");
  }

  public static void handleAreaEffect(AreaEffectConfig area, ModifierContext ctx, double radius) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleAreaEffect'");
  }

  public static void handleDamage(DamageConfig dmg, ModifierContext ctx) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleDamage'");
  }

  public static void handleBlockBreak(EventType eventType, ModifierContext ctx) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleBlockBreak'");
  }

  public static void applyStatusEffect(HitEffectProjectileConfig effectProj, LivingEntity target,
      World world) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'applyStatusEffect'");
  }

  public static void applyStatusEffect(HitEffectMeleeConfig effect, LivingEntity target, World world) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'applyStatusEffect'");
  }

  public static void handleProjectileEvent(EventType eventType, ModifierContext ctx) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleProjectileEvent'");
  }

  public static void handleMeleeEvent(EventType eventType, ModifierContext ctx) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleMeleeEvent'");
  }
}
