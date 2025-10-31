package name.modid.core.content.screen.slots;

import name.modid.core.content.screen.JewelryTableScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ResultSlot extends Slot {
  private final JewelryTableScreenHandler handler;

  public ResultSlot(JewelryTableScreenHandler handler, Inventory inv, int index, int x, int y) {
    super(inv, index, x, y);
    this.handler = handler;
  }

  @Override
  public boolean canInsert(ItemStack stack) {
    return false;
  }

  @Override
  public void onTakeItem(PlayerEntity player, ItemStack taken) {
    super.onTakeItem(player, taken);

    if (!player.getWorld().isClient) {
      var world = player.getWorld();
      var pos = handler.getInventory() instanceof net.minecraft.block.entity.BlockEntity be
          ? be.getPos()
          : player.getBlockPos();

      world.playSound(
          null,
          pos,
          net.minecraft.sound.SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
          net.minecraft.sound.SoundCategory.BLOCKS,
          1.0f,
          1.0f);
      world.playSound(null, pos, net.minecraft.sound.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
          net.minecraft.sound.SoundCategory.BLOCKS, 0.6f, 1.2f);
    }

    handler.consumeInputs();
    handler.updateResult();
    handler.sendContentUpdates();
  }
}