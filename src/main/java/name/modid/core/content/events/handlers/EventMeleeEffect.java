package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectMeleeConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class EventMeleeEffect {
  public static ActionResult setupEvent(PlayerEntity player, World world, Hand hand, Entity entity,
      EntityHitResult hitResult) {
    if (!(world instanceof ServerWorld serverWorld)
        || !(entity instanceof LivingEntity target)
        || !(player instanceof ServerPlayerEntity serverPlayer)) {
      return ActionResult.PASS;
    }

    return apply(serverPlayer, serverWorld, target);
  }

  public static ActionResult apply(ServerPlayerEntity serverPlayer, ServerWorld serverWorld, LivingEntity target) {
    return apply(serverPlayer, serverWorld, target, 0.0F);
  }

  public static ActionResult apply(ServerPlayerEntity serverPlayer,
      ServerWorld serverWorld,
      LivingEntity target,
      float damage) {
    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer,
        HitEffectMeleeConfig.class);

    if (modifiers.isEmpty()) {
      return ActionResult.PASS;
    }

    ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(serverPlayer)
        .withTarget(target)
        .withBaseDamageTaken(damage);
    ModifierContext ctx = ctxBuilder.build();
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);

    return ctx.getActionResult();
  }
}
