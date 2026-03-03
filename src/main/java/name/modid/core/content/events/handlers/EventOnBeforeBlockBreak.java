package name.modid.core.content.events.handlers;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.BeforeBlockBreakConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EventOnBeforeBlockBreak {
  public static boolean setupEvent(World world, PlayerEntity player, BlockPos pos, BlockState state,
      BlockEntity blockEntity) {
    if (!(world instanceof ServerWorld serverWorld)
        || !(player instanceof ServerPlayerEntity serverPlayer)) {
      return true;
    }

    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        serverPlayer,
        BeforeBlockBreakConfig.class);

    if (modifiers.isEmpty()) {
      return true;
    }

    ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
        .withOwner(serverPlayer)
        .withBlockPos(pos)
        .withBlockState(state);
    ModifierContext ctx = ctxBuilder.build();
    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);

    return !ctx.isCancelled();

  }
}
