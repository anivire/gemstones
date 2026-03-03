package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.HitMeleeConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class EventOnHitMelee {
  public static float setupEvent(
      ServerPlayerEntity serverPlayer,
      LivingEntity target,
      float damage) {
    if (!(serverPlayer.getWorld() instanceof ServerWorld serverWorld)) {
      return damage;
    }

    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer, HitMeleeConfig.class);

    if (modifiers.isEmpty()) {
      return damage;
    }

    ModifierContext ctx = new ModifierContext.ContextBuilder(serverWorld)
        .withOwner(serverPlayer)
        .withTarget(target)
        .withBaseDamageTaken(damage)
        .build();
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);

    return ctx.getDamageResult();
  }
}
