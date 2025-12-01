package name.modid.core.content.events;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.AfterDeathConfig;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EventAfterDeath {
  public static void setupEvent(LivingEntity entity, DamageSource damageSource) {
    World world = entity.getWorld();

    if (world instanceof ServerWorld serverWorld
        && damageSource.getAttacker() instanceof LivingEntity owner) {
      List<GemstoneModifier> modifiers = ModifierUtils.collectValuesFromArmor(
          (ServerPlayerEntity) damageSource.getAttacker(),
          armorPiece -> ModifierGatheringHelper.getModifiers(armorPiece, AfterDeathConfig.class));

      if (modifiers.isEmpty())
        return;

      ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
          .withOwner(owner)
          .withTarget(entity);
      ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctxBuilder.build());
    }
  }
}
