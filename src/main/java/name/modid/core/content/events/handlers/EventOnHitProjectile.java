package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.HitProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.ModifierUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class EventOnHitProjectile {
  public static void setupEvent(
      PersistentProjectileEntity projectile,
      ServerPlayerEntity serverPlayer,
      HitResult hitResult) {
    if (!(serverPlayer.getWorld() instanceof ServerWorld serverWorld)) {
      return;
    }

    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer, HitProjectileConfig.class);

    if (modifiers.isEmpty()) {
      return;
    }

    ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(serverPlayer)
        .withProjectile(projectile)
        .withBlockPos(BlockPos.ofFloored(hitResult.getPos()));

    if (hitResult instanceof EntityHitResult ehr
        && ehr.getEntity() instanceof LivingEntity living) {
      ctxBuilder.withTarget(living);
    }

    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctxBuilder.build());
  }
}