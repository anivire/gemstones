package name.modid.core.content.events;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.PlayerConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class EventPlayer {
  public static boolean setupEvent(LivingEntity entity, DamageSource source, float amount) {
    if (entity.getWorld() instanceof ServerWorld serverWorld &&
        source.getAttacker() instanceof LivingEntity owner &&
        entity instanceof ServerPlayerEntity player) {
      List<GemstoneModifier> modifiers = ModifierUtils.collectValuesFromArmor(
          player,
          armorPiece -> ModifierGatheringHelper.getModifiers(armorPiece, PlayerConfig.class));

      if (modifiers.isEmpty()) {
        return true;
      }

      PersistentProjectileEntity proj = null;
      if (source.getSource() instanceof PersistentProjectileEntity p) {
        proj = p;
      }

      ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
          .withOwner(owner)
          .withProjectile(proj)
          .withTarget(entity);

      ModifierContext ctx = ctxBuilder.build();
      ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);

      // if (modifiers
      // .stream()
      // .filter(x -> x.getConfig() instanceof PlayerConfig c
      // && c.eventType() == EventType.PLAYER_PROJECTILE_IMMUNE)
      // .count() == 0) {
      // return true;
      // } else {
      return ctx.getIsHurtable();
    }

    return true;
  }
}