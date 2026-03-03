package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.PlayerConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class EventPlayer {
  public static boolean setupEvent(LivingEntity entity, DamageSource source, float amount) {
    if (!(entity.getWorld() instanceof ServerWorld serverWorld)
        || !(source.getAttacker() instanceof ServerPlayerEntity serverPlayer)) {
      return true;
    }

    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer,
        PlayerConfig.class);

    if (modifiers.isEmpty()) {
      return true;
    }

    ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(serverPlayer)
        .withTarget(entity);

    if (source.getSource() instanceof PersistentProjectileEntity pojectile) {
      ctxBuilder.withProjectile(pojectile);
    }

    ModifierContext ctx = ctxBuilder.build();
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);

    return ctx.getIsHurtable();
  }

  public static void setupEventEndTick(ServerPlayerEntity serverPlayer) {
    if (!(serverPlayer.getWorld() instanceof ServerWorld serverWorld)) {
      return;
    }

    List<GemstoneModifier> modifiers = new ArrayList<>(
        ModifierUtils.collectGemstoneModifiersFromAllEquipment(serverPlayer, PlayerConfig.class));

    modifiers.removeIf(x -> {
      if (x.getConfig() instanceof PlayerConfig c) {
        return !c.eventType().getName().startsWith("PLAYER_TICK_");
      }
      return true;
    });

    if (modifiers.isEmpty()) {
      return;
    }

    ContextBuilder ctxBuilder = new ContextBuilder(serverWorld).withOwner(serverPlayer);
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctxBuilder.build());
  }
}