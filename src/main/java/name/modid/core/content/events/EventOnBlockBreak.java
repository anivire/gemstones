package name.modid.core.content.events;

import java.util.ArrayList;

import name.modid.core.api.modifiers.ModifierManagerLegacy;
import name.modid.core.api.modifiers.categories.ModifierOnBlockBreak;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
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

    ModifierManagerLegacy.applyOnBlockBreakModifiers(modifiers, player, world, state, pos);
  }
}
