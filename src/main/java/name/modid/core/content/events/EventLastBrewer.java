package name.modid.core.content.events;

import name.modid.core.utils.accessors.BrewingStandBlockEntityAccess;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EventLastBrewer {
  public static ActionResult setup(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
    if (world instanceof ServerWorld serverWorld) {
      BlockPos pos = hitResult.getBlockPos();
      if (!(serverWorld.getBlockState(pos).getBlock() instanceof BrewingStandBlock)) {
        return ActionResult.PASS;
      }

      if (serverWorld.getBlockEntity(pos) instanceof BrewingStandBlockEntity stand) {
        ItemStack ingredient = stand.getStack(3);
        if (!ingredient.isEmpty()) {
          ((BrewingStandBlockEntityAccess) (Object) stand)
              .setLastBrewer(player.getUuid());
        }
      }
    }

    return ActionResult.PASS;
  }
}
