package name.modid.core.content.events;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.HitProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;

public class EventOnHitProjectile {
  public static void setupEvent(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken,
      boolean blocked) {
    if (entity.getWorld() instanceof ServerWorld serverWorld
        && source.getAttacker() instanceof LivingEntity owner
        && source.getSource() instanceof PersistentProjectileEntity proj) {
      List<GemstoneModifier> modifiers = ModifierGatheringHelper.getModifiers(
          source.getWeaponStack(),
          HitProjectileConfig.class);

      if (modifiers.isEmpty()) {
        return;
      }

      ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
          .withOwner(owner)
          .withBaseDamageTaken(baseDamageTaken)
          .withProjectile(proj)
          .withBlockPos(proj.getBlockPos())
          .withTarget(entity);
      ModifierContext ctx = ctxBuilder.build();
      ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);
    }
  }
}
