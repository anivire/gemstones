package name.modid.core.content.events;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EventProjectileEffect {
  public static void setup(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken,
      boolean blocked) {
    World world = entity.getWorld();

    if (world instanceof ServerWorld serverWorld
        && source.getSource() instanceof ArrowEntity arrow
        && entity instanceof LivingEntity target
        && arrow.getOwner() instanceof LivingEntity owner) {
      List<GemstoneModifier> modifiers = ModifierGatheringHelper.getModifiers(
          source.getWeaponStack(),
          HitEffectProjectileConfig.class);

      if (modifiers.isEmpty())
        return;

      ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
          .withOwner(owner)
          .withProjectile(arrow)
          .withTarget(target);
      ModifierContext ctx = ctxBuilder.build();
      ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);
    }
  }
}
