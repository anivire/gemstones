package name.modid.helpers.events;

import java.util.ArrayList;

import name.modid.helpers.GemstoneSocketingHelper;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.category.ModifierOnBlockBreak;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EventOnBeforeBlockBreak {
  public static boolean setupEvent(World world, PlayerEntity player, BlockPos pos, BlockState state,
      BlockEntity blockEntity) {
    ItemStack itemStack = player.getMainHandStack();
    ArrayList<ModifierOnBlockBreak> modifiers = ModifierHelper.getOnBlockBreakModifiers(itemStack);

    if (modifiers.isEmpty()) {
      return true;
    }

    return GemstoneSocketingHelper.applyOnBeforeBlockBreakModifiers(modifiers, player, world, state, pos);
  }
}
