package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.HitEffectProjectileConfig;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.ModifierUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class EventProjectileEffect {
  public static void setup(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken,
      boolean blocked) {
    if (!(entity.getWorld() instanceof ServerWorld serverWorld)
        || !(source.getSource() instanceof ArrowEntity arrow)
        || !(entity instanceof LivingEntity target)
        || !(arrow.getOwner() instanceof ServerPlayerEntity serverPlayer)) {
      return;
    }

    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer,
        HitEffectProjectileConfig.class);

    if (modifiers.isEmpty()) {
      return;
    }

    ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(serverPlayer)
        .withProjectile(arrow)
        .withTarget(target);
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctxBuilder.build());

  }
}
