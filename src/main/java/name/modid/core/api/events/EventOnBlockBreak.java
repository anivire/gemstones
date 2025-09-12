package name.modid.core.api.events;

import java.util.ArrayList;

import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.impl.categories.ModifierOnBlockBreak;
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
    ArrayList<ModifierOnBlockBreak> modifiers = ModifierGatheringHelper.getOnBlockBreakModifiers(itemStack);

    if (modifiers.isEmpty()) {
      return;
    }

    GemstoneSlotHelper.applyOnBlockBreakModifiers(modifiers, player, world, state, pos);
  }
}
