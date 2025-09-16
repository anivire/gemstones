package name.modid.core.api.modifiers.managers;

import name.modid.core.api.modifiers.config.ModifierConfig.AreaEffectConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.CustomConditionConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.DamageConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectMeleeConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectProjectileConfig;
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class ModifierHandler {
  public static void handleCustom(EventType eventType, CustomConditionConfig cond, ModifierContext ctx) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleCustom'");
  }

  public static void handleAreaEffect(AreaEffectConfig config, ModifierContext ctx, double radius) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleAreaEffect'");
  }

  public static void handleDamage(DamageConfig config, ModifierContext ctx) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'handleDamage'");
  }

  public static void applyStatusEffect(HitEffectProjectileConfig config, LivingEntity target,
      World world) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'applyStatusEffect'");
  }

  public static void applyStatusEffect(HitEffectMeleeConfig config, LivingEntity target, World world) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'applyStatusEffect'");
  }
}
