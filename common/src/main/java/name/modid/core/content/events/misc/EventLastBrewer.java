package name.modid.core.content.events.misc;

import name.modid.core.utils.accessors.BrewingStandBlockEntityAccess;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EventLastBrewer {
  public static ActionResult setup(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
    return setup(player, world, hand, hitResult.getBlockPos());
  }

  public static ActionResult setup(PlayerEntity player, World world, Hand hand, BlockPos pos) {
    if (!(world instanceof ServerWorld serverWorld)
        || !(serverWorld.getBlockState(pos).getBlock() instanceof BrewingStandBlock)
        || !(serverWorld.getBlockEntity(pos) instanceof BrewingStandBlockEntity stand)) {
      return ActionResult.PASS;
    }

    ((BrewingStandBlockEntityAccess) (Object) stand)
        .setLastBrewer(player.getUuid());

    return ActionResult.PASS;
  }
}
