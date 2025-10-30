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
        source.getSource() instanceof PersistentProjectileEntity proj &&
        entity instanceof ServerPlayerEntity player) {
      List<GemstoneModifier> modifiers = ModifierUtils.collectValuesFromArmor(
          player,
          armorPiece -> ModifierGatheringHelper.getModifiers(armorPiece, PlayerConfig.class));

      if (modifiers.isEmpty()) {
        return true;
      }

      ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
          .withOwner(owner)
          .withProjectile(proj)
          .withTarget(entity);

      ModifierContext ctx = ctxBuilder.build();
      ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);

      return ctx.getIsHurtable();
    }

    return true;
  }
}