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
    boolean removeBreaks = handler.prepareRemoveBreak(player);

    super.onTakeItem(player, taken);

    handler.finalizeTakeResult(player);

    if (removeBreaks) {
      taken.setCount(0);
    }

    handler.updateOutputs();
    handler.sendContentUpdates();
  }
}
