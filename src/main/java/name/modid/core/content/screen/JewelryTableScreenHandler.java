package name.modid.core.content.screen;

import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.content.items.GemstoneItem;
import name.modid.core.content.screen.slots.GemstoneSlot;
import name.modid.core.content.screen.slots.ResultSlot;
import name.modid.core.content.screen.slots.ToolSlot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class JewelryTableScreenHandler extends ScreenHandler implements InventoryChangedListener {
  private final Inventory inventory;

  public JewelryTableScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
    this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(pos));
  }

  public JewelryTableScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
    super(ScreenRegistry.JEWELRY_TABLE_SCREEN_HANDLER, syncId);
    this.inventory = (Inventory) blockEntity;

    checkSize(this.inventory, 3);

    addMonitoredSlot(new ToolSlot(this.inventory, 0, 53, 35)); // вход 1
    addMonitoredSlot(new GemstoneSlot(this.inventory, 1, 71, 35)); // вход 2
    this.addSlot(new ResultSlot(this, this.inventory, 2, 125, 35)); // выход

    addPlayerInventory(playerInventory);
    addPlayerHotbar(playerInventory);

    updateResult();
    sendContentUpdates();
  }

  private void addMonitoredSlot(Slot base) {
    this.addSlot(new Slot(base.inventory, base.getIndex(), base.x, base.y) {
      @Override
      public boolean canInsert(ItemStack stack) {
        return base.canInsert(stack);
      }

      @Override
      public void markDirty() {
        super.markDirty();
        updateResult();
        sendContentUpdates();
      }
    });
  }

  @Override
  public void onInventoryChanged(Inventory inv) {
    updateResult();
    sendContentUpdates();
  }

  public void updateResult() {
    ItemStack base = this.inventory.getStack(0);
    ItemStack gem = this.inventory.getStack(1);

    if (base.isEmpty()
        || gem.isEmpty()
        || !(gem.getItem() instanceof GemstoneItem)
        || !GemstoneSlotHelper.isItemValid(base.getItem())) {
      this.inventory.setStack(2, ItemStack.EMPTY);
      return;
    }

    Integer emptySlot = GemstoneSlotHelper.getFirstEmptySlotIndex(base);
    if (emptySlot == null) {
      this.inventory.setStack(2, ItemStack.EMPTY);
      return;
    }

    ItemStack resultCopy = base.copy();
    ItemStack modified = GemstoneSlotHelper.setGemstoneByIndex(resultCopy, emptySlot, (GemstoneItem) gem.getItem());

    this.inventory.setStack(2, modified != null ? modified : ItemStack.EMPTY);
  }

  public void consumeInputs() {
    ItemStack gem = this.inventory.getStack(1);
    if (!gem.isEmpty()) {
      gem.decrement(1);
      this.inventory.setStack(1, gem);
    }

    ItemStack base = this.inventory.getStack(0);
    if (!base.isEmpty()) {
      base.decrement(1);
      this.inventory.setStack(0, base.isEmpty() ? ItemStack.EMPTY : base);
    }
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

  @Override
  public ItemStack quickMove(PlayerEntity player, int invSlot) {
    ItemStack newStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(invSlot);
    if (slot == null || !slot.hasStack())
      return ItemStack.EMPTY;

    ItemStack original = slot.getStack();
    newStack = original.copy();

    if (invSlot == 2) {
      if (!this.insertItem(original, this.inventory.size(), this.slots.size(), true)) {
        return ItemStack.EMPTY;
      }
      this.consumeInputs();
      this.updateResult();
      this.sendContentUpdates();
      slot.markDirty();
      return newStack;
    }

    if (invSlot < this.inventory.size()) {
      if (!this.insertItem(original, this.inventory.size(), this.slots.size(), true))
        return ItemStack.EMPTY;
    } else {
      if (!this.insertItem(original, 0, this.inventory.size(), false))
        return ItemStack.EMPTY;
    }

    if (original.isEmpty())
      slot.setStack(ItemStack.EMPTY);
    else
      slot.markDirty();

    return newStack;
  }

  public Inventory getInventory() {
    return this.inventory;
  }
}