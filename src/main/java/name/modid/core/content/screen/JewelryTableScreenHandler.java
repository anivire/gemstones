package name.modid.core.content.screen;

import name.modid.core.content.screen.slots.GemstoneSlot;
import name.modid.core.content.screen.slots.ResultSlot;
import name.modid.core.content.screen.slots.ToolSlot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class JewelryTableScreenHandler extends ScreenHandler {
  private final Inventory inventory;

  public JewelryTableScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos blockPos) {
    this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(blockPos));
  }

  public JewelryTableScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
    super(ScreenRegistry.JEWELRY_TABLE_SCREEN_HANDLER, syncId);
    this.inventory = ((Inventory) blockEntity);

    checkSize(this.inventory, 3);

    this.addSlot(new ToolSlot(this.inventory, 0, 44, 35));
    this.addSlot(new GemstoneSlot(this.inventory, 1, 62, 35));
    this.addSlot(new ResultSlot(this.inventory, 2, 116, 35));

    addPlayerInventory(playerInventory);
    addPlayerHotbar(playerInventory);
  }

  @Override
  public ItemStack quickMove(PlayerEntity player, int invSlot) {
    ItemStack newStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(invSlot);
    if (slot != null && slot.hasStack()) {
      ItemStack originalStack = slot.getStack();
      newStack = originalStack.copy();
      if (invSlot < this.inventory.size()) {
        if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
        return ItemStack.EMPTY;
      }

      if (originalStack.isEmpty()) {
        slot.setStack(ItemStack.EMPTY);
      } else {
        slot.markDirty();
      }
    }
    return newStack;
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return this.inventory.canPlayerUse(player);
  }

  private void addPlayerInventory(PlayerInventory playerInventory) {
    for (int i = 0; i < 3; ++i) {
      for (int l = 0; l < 9; ++l) {
        this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 90 + i * 18));
      }
    }
  }

  private void addPlayerHotbar(PlayerInventory playerInventory) {
    for (int i = 0; i < 9; ++i) {
      this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 148));
    }
  }
}