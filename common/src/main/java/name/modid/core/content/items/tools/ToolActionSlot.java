package name.modid.core.content.items.tools;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ToolActionSlot extends Slot {
  public ToolActionSlot(Inventory inv, int index, int x, int y) {
    super(inv, index, x, y);
  }

  @Override
  public boolean canInsert(ItemStack stack) {
    return stack.getItem() instanceof ChiselItem || stack.getItem() instanceof JewelryPliersItem;
  }
}