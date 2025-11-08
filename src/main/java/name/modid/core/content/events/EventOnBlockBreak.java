package name.modid.core.content.events;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.BlockBreakConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EventOnBlockBreak {
  public static void setupEvent(World world, PlayerEntity player, BlockPos pos, BlockState state,
      BlockEntity blockEntity) {
    if (world instanceof ServerWorld serverWorld
        && player instanceof LivingEntity owner) {
      List<GemstoneModifier> modifiers = ModifierGatheringHelper.getModifiers(
          // TODO: needed mainHandStack or enough this?
          player.getMainHandStack(),
          BlockBreakConfig.class);

      if (modifiers.isEmpty()) {
        return;
      }

      ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
          .withOwner(owner)
          .withBlockPos(pos)
          .withBlockState(state);
      ModifierContext ctx = ctxBuilder.build();
      ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);
    }
  }
}
