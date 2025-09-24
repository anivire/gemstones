package name.modid.core.content.events;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.AreaEffectConfig;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.utils.Utils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EventAreaEffect {
  public static void setupEvent(ServerPlayerEntity player) {
    World world = player.getWorld();

    if (world instanceof ServerWorld serverWorld) {
      List<GemstoneModifier> modifiers = Utils.collectPlayerArmorValues(
          player,
          armorPiece -> ModifierGatheringHelper.getModifiers(armorPiece, AreaEffectConfig.class));

      if (modifiers.isEmpty())
        return;

      ContextBuilder ctxBuilder = new ContextBuilder(serverWorld).withOwner(player);
      ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctxBuilder.build());
    }
  }
}