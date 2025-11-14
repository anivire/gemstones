package name.modid.core.content.screen.slots;

import name.modid.core.content.items.tools.ChiselItem;
import name.modid.core.content.items.tools.JewelryHammerItem;
import name.modid.core.content.items.tools.JewelryPliersItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ToolActionSlot extends Slot {
  public ToolActionSlot(Inventory inventory, int index, int x, int y) {
    super(inventory, index, x, y);
  }

  @Override
  public boolean canInsert(ItemStack stack) {
    return stack.getItem() instanceof ChiselItem
        || stack.getItem() instanceof JewelryPliersItem
        || stack.getItem() instanceof JewelryHammerItem;
  }
}