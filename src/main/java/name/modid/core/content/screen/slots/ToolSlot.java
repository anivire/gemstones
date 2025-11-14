package name.modid.core.content.screen.slots;

import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ToolSlot extends Slot {
  public ToolSlot(Inventory inventory, int index, int x, int y) {
    super(inventory, index, x, y);
  }

  @Override
  public boolean canInsert(ItemStack stack) {
    Item item = stack.getItem();
    return isItemValid(item);
  }

  public static boolean isItemValid(Item item) {
    return GemstoneSlotHelper.isItemValid(item);
  }
}