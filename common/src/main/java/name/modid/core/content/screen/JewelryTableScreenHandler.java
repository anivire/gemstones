package name.modid.core.content.screen;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.api.modifiers.helpers.ModifierHelper;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.content.items.ExpansionCrystalItem;
import name.modid.core.content.items.GemstoneItem;
import name.modid.core.content.items.tools.ChiselItem;
import name.modid.core.content.items.tools.JewelryHammerItem;
import name.modid.core.content.items.tools.JewelryPliersItem;
import name.modid.core.content.screen.slots.GemstoneSlot;
import name.modid.core.content.screen.slots.ResultSlot;
import name.modid.core.content.screen.slots.ToolActionSlot;
import name.modid.core.content.screen.slots.ToolSlot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class JewelryTableScreenHandler extends ScreenHandler implements InventoryChangedListener {
  public static final int SLOT_ACTION = 0;
  public static final int SLOT_BASE = 1;
  public static final int SLOT_GEM = 2;
  public static final int SLOT_RESULT = 3;
  private static final int TABLE_SLOT_COUNT = 4;
  private static final int TABLE_INVENTORY_SIZE = 3;

  private final Inventory inventory;
  private final Inventory resultInventory = new SimpleInventory(1);
  private final BlockPos pos;

  public JewelryTableScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
    this(syncId, playerInventory, buf.readBlockPos());
  }

  public JewelryTableScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
    this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(pos));
  }

  public JewelryTableScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
    super(ScreenRegistry.JEWELRY_TABLE_SCREEN_HANDLER.get(), syncId);
    this.inventory = (Inventory) blockEntity;
    this.pos = blockEntity.getPos();

    checkSize(this.inventory, TABLE_INVENTORY_SIZE);

    addMonitoredSlot(new ToolActionSlot(this.inventory, SLOT_ACTION, 35, 35));
    addMonitoredSlot(new ToolSlot(this.inventory, SLOT_BASE, 53, 35));
    addMonitoredSlot(new GemstoneSlot(this.inventory, SLOT_GEM, 71, 35));
    this.addSlot(new ResultSlot(this, this.resultInventory, 0, 125, 35));

    addPlayerInventory(playerInventory);
    addPlayerHotbar(playerInventory);

    updateOutputs();
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
        updateOutputs();
        sendContentUpdates();
      }
    });
  }

  @Override
  public void onInventoryChanged(Inventory inv) {
    updateOutputs();
    sendContentUpdates();
  }

  public enum ActionMode {
    INSERT, EXPAND, REMOVE, NONE
  }

  private ActionMode getMode() {
    ItemStack tool = inventory.getStack(SLOT_ACTION);
    ItemStack gemSlot = inventory.getStack(SLOT_GEM);

    if (tool.isEmpty())
      return ActionMode.NONE;

    if (tool.getItem() instanceof ChiselItem
        && gemSlot.getItem() instanceof ExpansionCrystalItem)
      return ActionMode.EXPAND;

    if (tool.getItem() instanceof JewelryHammerItem
        && gemSlot.getItem() instanceof GemstoneItem)
      return ActionMode.INSERT;

    if (tool.getItem() instanceof JewelryPliersItem)
      return ActionMode.REMOVE;

    return ActionMode.NONE;
  }

  public ActionMode getCurrentMode() {
    return getMode();
  }

  public boolean canTakeResult(PlayerEntity player) {
    if (getMode() == ActionMode.NONE || resultInventory.getStack(0).isEmpty()) {
      return false;
    }

    return player.getAbilities().creativeMode || player.experienceLevel >= getLevelCost();
  }

  public int getLevelCost() {
    if (resultInventory.getStack(0).isEmpty()) {
      return 0;
    }

    return switch (getMode()) {
      case INSERT -> getInsertLevelCost();
      case REMOVE -> getRemoveLevelCost();
      default -> 0;
    };
  }

  private int getInsertLevelCost() {
    if (!(inventory.getStack(SLOT_GEM).getItem() instanceof GemstoneItem gem)) {
      return 0;
    }

    return gem.getRarityType().getInsertLevelCost();
  }

  private int getRemoveLevelCost() {
    ItemStack base = inventory.getStack(SLOT_BASE);
    int index = GemstoneSlotHelper.getLastFilledSlotIndex(base);
    GemstoneComponent[] gems = GemstoneSlotHelper.getGemstones(base);
    if (index < 0 || index >= gems.length || gems[index].gemstoneQualityType() == null) {
      return 0;
    }

    return gems[index].gemstoneQualityType().getRemoveLevelCost();
  }

  public void updateOutputs() {
    ActionMode mode = getMode();
    switch (mode) {
      case INSERT -> buildInsertResult();
      case REMOVE -> buildRemoveResult();
      case EXPAND -> buildExpandResult();
      default -> setResult(ItemStack.EMPTY);
    }
  }

  private void setResult(ItemStack stack) {
    resultInventory.setStack(0, stack);

    if (SLOT_RESULT >= 0 && SLOT_RESULT < this.slots.size()) {
      Slot resultSlot = this.slots.get(SLOT_RESULT);
      resultSlot.setStack(stack);
      resultSlot.markDirty();
    }
  }

  private void buildExpandResult() {
    ItemStack base = inventory.getStack(SLOT_BASE);
    ItemStack crystal = inventory.getStack(SLOT_GEM);

    if (base.isEmpty()
        || crystal.isEmpty()
        || !(crystal.getItem() instanceof ExpansionCrystalItem)) {
      setResult(ItemStack.EMPTY);
      return;
    }

    if (!GemstoneSlotHelper.isItemValid(base.getItem())) {
      setResult(ItemStack.EMPTY);
      return;
    }

    if (!GemstoneSlotHelper.canAddNewSlot(base)) {
      setResult(ItemStack.EMPTY);
      return;
    }

    ItemStack result = GemstoneSlotHelper.addNewGemSlot(base.copy());
    setResult(result);
  }

  private void buildInsertResult() {
    ItemStack base = inventory.getStack(SLOT_BASE);
    ItemStack gem = inventory.getStack(SLOT_GEM);

    if (base.isEmpty()
        || gem.isEmpty()
        || !(gem.getItem() instanceof GemstoneItem)
        || !GemstoneSlotHelper.isItemValid(base.getItem())) {
      setResult(ItemStack.EMPTY);

      return;
    }

    GemstoneItem gemItem = (GemstoneItem) gem.getItem();
    GemstoneType gemType = gemItem.getType();
    GemstoneQuality gemQuality = gemItem.getRarityType();

    // A gemstone without an effect for this item category would sit in the socket doing nothing
    if (ModifierHelper.getGemstoneModifierForItem(gemType, gemQuality, base.getItem()) == null) {
      setResult(ItemStack.EMPTY);
      return;
    }

    Map<GemstoneType, GemstoneQuality> g = Arrays.stream(GemstoneSlotHelper.getGemstones(base))
        .collect(Collectors.toMap(
            GemstoneComponent::gemstoneType,
            GemstoneComponent::gemstoneQualityType,
            (q1, q2) -> q1));

    // Forbid inserting two equal gemstone types with mythic quality
    if (gemQuality == GemstoneQuality.MYTHIC) {
      if (g.containsKey(gemType) && g.get(gemType) == GemstoneQuality.MYTHIC) {
        setResult(ItemStack.EMPTY);
        return;
      }
    }

    Integer emptySlot = GemstoneSlotHelper.getFirstEmptySlotIndex(base);

    if (emptySlot == null) {
      setResult(ItemStack.EMPTY);
      return;
    }

    ItemStack copy = base.copy();
    ItemStack modified = GemstoneSlotHelper.setGemstoneByIndex(copy, emptySlot, (GemstoneItem) gem.getItem());

    setResult(modified != null ? modified : ItemStack.EMPTY);
  }

  private void buildRemoveResult() {
    ItemStack base = inventory.getStack(SLOT_BASE);

    if (base.isEmpty()
        || !GemstoneSlotHelper.isItemValid(base.getItem())) {
      setResult(ItemStack.EMPTY);
      return;
    }

    int index = GemstoneSlotHelper.getLastFilledSlotIndex(base);

    if (index == -1) {
      setResult(ItemStack.EMPTY);
      return;
    }

    ItemStack previewGem = GemstoneSlotHelper.makeGemItemFromSocket(base, index);
    setResult(previewGem);
  }

  public void finalizeTakeResult(PlayerEntity player) {
    ActionMode mode = getMode();

    if (mode == ActionMode.INSERT) {
      chargeLevels(player, getLevelCost());

      ItemStack gem = inventory.getStack(SLOT_GEM);

      if (!gem.isEmpty()) {
        gem.decrement(1);
        inventory.setStack(SLOT_GEM, gem);
      }

      ItemStack base = inventory.getStack(SLOT_BASE);

      if (!base.isEmpty()) {
        base.decrement(1);
        inventory.setStack(SLOT_BASE, base.isEmpty() ? ItemStack.EMPTY : base);
      }

      damageActionTool(player, 1);
      playInsertSound(player);
    } else if (mode == ActionMode.REMOVE) {
      ItemStack base = inventory.getStack(SLOT_BASE);
      int idx = GemstoneSlotHelper.getLastFilledSlotIndex(base);

      if (idx != -1) {
        chargeLevels(player, getLevelCost());

        GemstoneSlotHelper.clearGemstoneAtIndex(base, idx);
        inventory.setStack(SLOT_BASE, base);
        sendContentUpdates();

        playRemoveSound(player);
      }

      damageActionTool(player, 1);
    } else if (mode == ActionMode.EXPAND) {
      ItemStack base = inventory.getStack(SLOT_BASE);
      ItemStack crystal = inventory.getStack(SLOT_GEM);

      if (!base.isEmpty() && !crystal.isEmpty() && GemstoneSlotHelper.canAddNewSlot(base)) {
        ItemStack modified = GemstoneSlotHelper.addNewGemSlot(base.copy());

        setResult(modified);

        base.decrement(1);
        if (base.isEmpty()) {
          inventory.setStack(SLOT_BASE, ItemStack.EMPTY);
        } else {
          inventory.setStack(SLOT_BASE, base);
        }

        crystal.decrement(1);
        if (crystal.isEmpty()) {
          inventory.setStack(SLOT_GEM, ItemStack.EMPTY);
        } else {
          inventory.setStack(SLOT_GEM, crystal);
        }

        playExpandSound(player);
      }

      damageActionTool(player, 1);
      sendContentUpdates();
    }
  }

  private void chargeLevels(PlayerEntity player, int cost) {
    if (!player.getAbilities().creativeMode && cost > 0) {
      player.addExperienceLevels(-cost);
    }
  }

  private void damageActionTool(PlayerEntity player, int amount) {
    ItemStack tool = inventory.getStack(SLOT_ACTION);
    if (tool.isEmpty())
      return;

    tool.damage(amount, player, net.minecraft.entity.EquipmentSlot.MAINHAND);

    if (tool.isEmpty()) {
      inventory.setStack(SLOT_ACTION, ItemStack.EMPTY);
    } else {
      inventory.setStack(SLOT_ACTION, tool);
    }

    try {
      inventory.markDirty();
    } catch (Throwable ignored) {
    }

    if (SLOT_ACTION >= 0 && SLOT_ACTION < this.slots.size()) {
      this.slots.get(SLOT_ACTION).markDirty();
    }

    sendContentUpdates();
  }

  private void playInsertSound(PlayerEntity player) {
    if (player.getWorld().isClient)
      return;

    var w = player.getWorld();

    w.playSound(null, pos, SoundEvents.BLOCK_VAULT_INSERT_ITEM, SoundCategory.BLOCKS, 0.55f, 1.35f);
    w.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT, SoundCategory.BLOCKS, 0.55f, 0.75f);
  }

  private void playExpandSound(PlayerEntity player) {
    if (player.getWorld().isClient)
      return;

    var w = player.getWorld();

    w.playSound(null, pos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 0.9f, 0.95f);
    w.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT, SoundCategory.BLOCKS, 0.55f, 0.75f);
  }

  private void playRemoveSound(PlayerEntity player) {
    if (player.getWorld().isClient)
      return;

    var w = player.getWorld();

    w.playSound(null, pos, SoundEvents.ITEM_BUNDLE_REMOVE_ONE, SoundCategory.BLOCKS, 0.75f, 0.95f);
    w.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_CLUSTER_HIT, SoundCategory.BLOCKS, 0.55f, 0.75f);
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

    if (invSlot == SLOT_RESULT) {
      updateOutputs();
      if (!canTakeResult(player)) {
        sendContentUpdates();
        return ItemStack.EMPTY;
      }

      original = slot.getStack();
      newStack = original.copy();

      if (!this.insertItem(original, TABLE_SLOT_COUNT, this.slots.size(), true)) {
        return ItemStack.EMPTY;
      }

      finalizeTakeResult(player);

      this.updateOutputs();
      this.sendContentUpdates();

      slot.markDirty();

      return newStack;
    }

    if (invSlot < TABLE_SLOT_COUNT) {
      if (!this.insertItem(original, TABLE_SLOT_COUNT, this.slots.size(), true)) {
        return ItemStack.EMPTY;
      }
    } else {
      if (!this.insertItem(original, 0, TABLE_INVENTORY_SIZE, false)) {
        return ItemStack.EMPTY;
      }
    }

    if (original.isEmpty()) {
      slot.setStack(ItemStack.EMPTY);
    }

    slot.markDirty();
    updateOutputs();
    sendContentUpdates();

    return newStack;
  }

  public Inventory getInventory() {
    return this.inventory;
  }
}
