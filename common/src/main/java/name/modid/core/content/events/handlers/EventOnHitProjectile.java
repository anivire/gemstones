package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierConfig.HitProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class EventOnHitProjectile {
  // Before the hit: only modifiers that change the projectile damage
  public static void setupDamageEvent(
      PersistentProjectileEntity projectile,
      ServerPlayerEntity serverPlayer,
      HitResult hitResult) {
    if (!(serverPlayer.getWorld() instanceof ServerWorld serverWorld)) {
      return;
    }

    List<GemstoneModifier> damageModifiers = new ArrayList<>(ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer, HitProjectileConfig.class));
    damageModifiers.removeIf(modifier -> !isDamageModifier(modifier));

    if (damageModifiers.isEmpty()) {
      return;
    }

    ModifierManager.applyModifiers(new ArrayList<>(damageModifiers), buildContext(serverWorld, projectile, serverPlayer,
        hitResult));
  }

  // After the projectile actually damaged a living target
  public static void setupEvent(
      PersistentProjectileEntity projectile,
      ServerPlayerEntity serverPlayer,
      HitResult hitResult) {
    if (!(serverPlayer.getWorld() instanceof ServerWorld serverWorld)) {
      return;
    }

    ModifierContext ctx = buildContext(serverWorld, projectile, serverPlayer, hitResult);

    List<GemstoneModifier> hitModifiers = new ArrayList<>(ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer, HitProjectileConfig.class));
    hitModifiers.removeIf(EventOnHitProjectile::isDamageModifier);

    if (!hitModifiers.isEmpty()) {
      ModifierManager.applyModifiers(new ArrayList<>(hitModifiers), ctx);
    }

    List<GemstoneModifier> effectModifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer, HitEffectProjectileConfig.class);

    if (!effectModifiers.isEmpty()) {
      ModifierManager.applyModifiers(new ArrayList<>(effectModifiers), ctx);
    }
  }

  private static ModifierContext buildContext(
      ServerWorld serverWorld,
      PersistentProjectileEntity projectile,
      ServerPlayerEntity serverPlayer,
      HitResult hitResult) {
    ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(serverPlayer)
        .withProjectile(projectile)
        .withBlockPos(BlockPos.ofFloored(hitResult.getPos()));

    if (hitResult instanceof EntityHitResult ehr
        && ehr.getEntity() instanceof LivingEntity living) {
      ctxBuilder.withTarget(living);
    }

    return ctxBuilder.build();
  }

  private static boolean isDamageModifier(GemstoneModifier modifier) {
    return modifier.getConfig() instanceof HitProjectileConfig config
        && config.eventType() == EventType.ON_HIT_EXP_ADDITIONAL_DAMAGE;
  }
}
