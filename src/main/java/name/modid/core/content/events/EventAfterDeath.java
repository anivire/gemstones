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

    if (!(world instanceof ServerWorld serverWorld)) {
      return;
    }

    if (!(damageSource.getAttacker() instanceof ServerPlayerEntity player)) {
      return;
    }

    List<GemstoneModifier> modifiers = ModifierUtils.collectValuesFromArmor(
        player,
        armorPiece -> ModifierGatheringHelper.getModifiers(armorPiece, AfterDeathConfig.class));

    if (modifiers.isEmpty()) {
      return;
    }

    ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(player)
        .withTarget(entity);

    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctxBuilder.build());
  }

}
