package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.OnPlayerDamageConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class EventOnPlayerDamage {
  public static boolean setup(
      LivingEntity targetEntity,
      DamageSource source,
      float baseDamageTaken,
      float damageTaken,
      boolean blocked) {
    // Like the totem of undying, the lethal save does not work against /kill and the void
    if (!(targetEntity instanceof ServerPlayerEntity serverPlayer)
        || !(targetEntity.getWorld() instanceof ServerWorld serverWorld)
        || source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
      return true;
    }

    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer, OnPlayerDamageConfig.class);

    if (modifiers.isEmpty()) {
      return true;
    }

    ModifierContext.ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(serverPlayer)
        .withBaseDamageTaken(baseDamageTaken)
        .withTarget(source.getAttacker());

    if (source.getSource() instanceof PersistentProjectileEntity proj) {
      ctxBuilder.withProjectile(proj);
    }

    ModifierContext ctx = ctxBuilder.build();
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);
    return ctx.getIsHurtable();
  }
}
