package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.OnDamageConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class EventOnDamage {
  public static void setup(LivingEntity entity,
      DamageSource source,
      float baseDamageTaken,
      float damageTaken,
      boolean blocked) {
    if (!(source.getAttacker() instanceof ServerPlayerEntity serverPlayer)
        || !(entity.getWorld() instanceof ServerWorld serverWorld)
        || !(source.getSource() instanceof PersistentProjectileEntity proj)) {
      return;
    }

    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer, OnDamageConfig.class);

    if (modifiers.isEmpty()) {
      return;
    }

    ModifierContext.ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(serverPlayer)
        .withProjectile(proj)
        .withBaseDamageTaken(baseDamageTaken)
        .withTarget(entity);
    ModifierContext ctx = ctxBuilder.build();
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);
  }
}