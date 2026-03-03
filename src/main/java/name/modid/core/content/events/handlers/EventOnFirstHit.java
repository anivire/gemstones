package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.OnFirstHitConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class EventOnFirstHit {
  public static float setupEvent(LivingEntity target, DamageSource source, float amount) {
    if (!(target.getWorld() instanceof ServerWorld serverWorld)
        || !(source.getAttacker() instanceof ServerPlayerEntity serverPlayer)) {
      return amount;
    }

    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer,
        OnFirstHitConfig.class);

    if (modifiers.isEmpty()) {
      return amount;
    }

    ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(serverPlayer)
        .withBaseDamageTaken(amount)
        .withTarget(target);
    ModifierContext ctx = ctxBuilder.build();
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);

    return ctx.getDamageResult();
  }
}
