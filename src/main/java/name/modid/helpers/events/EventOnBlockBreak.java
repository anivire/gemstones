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

public class EventOnBlockBreak {
  public static void setupEvent(World world, PlayerEntity player, BlockPos pos, BlockState state,
      BlockEntity blockEntity) {
    ItemStack itemStack = player.getMainHandStack();
    ArrayList<ModifierOnBlockBreak> modifiers = ModifierHelper.getOnBlockBreakModifiers(itemStack);

    if (modifiers.isEmpty()) {
      return;
    }

    GemstoneSocketingHelper.applyOnBlockBreakModifiers(modifiers, player, world, state, pos);
  }
}
