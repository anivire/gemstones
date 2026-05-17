package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.AfterDeathConfig;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EventAfterDeath {
  public static void setupEvent(LivingEntity entity, DamageSource damageSource) {
    if (!(entity.getWorld() instanceof ServerWorld serverWorld)) {
      return;
    }

    detonateIfMarked(entity);

    if (!(damageSource.getAttacker() instanceof ServerPlayerEntity player)) {
      return;
    }

    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        player,
        AfterDeathConfig.class);

    if (modifiers.isEmpty()) {
      return;
    }

    ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(player)
        .withTarget(entity);
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctxBuilder.build());
  }

  private static void detonateIfMarked(LivingEntity entity) {
    if (!entity.hasStatusEffect(EffectsRegistry.DETONATE_EFFECT)) {
      return;
    }

    entity.getWorld().createExplosion(
        entity,
        null,
        null,
        entity.getX(),
        entity.getY(),
        entity.getZ(),
        4.0F + entity.getStatusEffect(EffectsRegistry.DETONATE_EFFECT).getAmplifier(),
        false,
        World.ExplosionSourceType.TNT);
  }
}
