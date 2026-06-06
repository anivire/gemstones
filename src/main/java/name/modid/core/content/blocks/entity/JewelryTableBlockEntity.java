package name.modid.core.content.blocks.entity;

import org.jetbrains.annotations.Nullable;

import name.modid.core.content.blocks.entity.core.BlockEntitiesRegistry;
import name.modid.core.content.blocks.entity.core.ImplementedInventory;
import name.modid.core.content.screen.JewelryTableScreenHandler;
import net.minecraft.block.Block;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import name.modid.core.content.items.ExpansionCrystalItem;
import name.modid.core.content.items.GemstoneItem;
import name.modid.core.content.items.tools.ChiselItem;
import name.modid.core.content.items.tools.JewelryHammerItem;
import name.modid.core.content.items.tools.JewelryPliersItem;
import name.modid.core.content.screen.slots.ToolSlot;

public class JewelryTableBlockEntity extends BlockEntity
    implements ImplementedInventory, ExtendedScreenHandlerFactory<BlockPos> {
  private static final int[] AUTOMATION_INPUT_SLOTS = {
      JewelryTableScreenHandler.SLOT_ACTION,
      JewelryTableScreenHandler.SLOT_BASE,
      JewelryTableScreenHandler.SLOT_GEM
  };

  private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);

  public JewelryTableBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntitiesRegistry.JEWELRY_TABLE_BLOCK_ENTITY, pos, state);
  }

  @Override
  public DefaultedList<ItemStack> getItems() {
    return inventory;
  }

  @Override
  public int[] getAvailableSlots(Direction side) {
    return AUTOMATION_INPUT_SLOTS;
  }

  @Override
  public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
    return canAutomationInsert(slot, stack);
  }

  @Override
  public boolean canExtract(int slot, ItemStack stack, Direction side) {
    return slot != JewelryTableScreenHandler.SLOT_RESULT;
  }

  public static boolean canAutomationInsert(int slot, ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }

    return canAutomationInsertItem(slot, stack.getItem());
  }

  static boolean canAutomationInsertItem(int slot, @Nullable Item item) {
    return switch (slot) {
      case JewelryTableScreenHandler.SLOT_ACTION -> item instanceof ChiselItem
          || item instanceof JewelryPliersItem
          || item instanceof JewelryHammerItem;
      case JewelryTableScreenHandler.SLOT_BASE -> item != null && ToolSlot.isItemValid(item);
      case JewelryTableScreenHandler.SLOT_GEM -> item instanceof GemstoneItem
          || item instanceof ExpansionCrystalItem;
      default -> false;
    };
  }

  @Override
  public void markDirty() {
    super.markDirty();

    if (world != null && !world.isClient) {
      BlockState state = getCachedState();
      world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
    }
  }

  @Override
  protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    super.writeNbt(nbt, registryLookup);
    Inventories.writeNbt(nbt, inventory, registryLookup);
  }

  @Override
  protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    super.readNbt(nbt, registryLookup);
    Inventories.readNbt(nbt, inventory, registryLookup);
  }

  @Override
  public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
    return this.pos;
  }

  @Override
  public Text getDisplayName() {
    return Text.empty();
  }

  @Override
  public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
    return new JewelryTableScreenHandler(syncId, playerInventory, this.pos);
  }

  @Nullable
  @Override
  public Packet<ClientPlayPacketListener> toUpdatePacket() {
    return BlockEntityUpdateS2CPacket.create(this);
  }

  @Override
  public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
    return createNbt(registryLookup);
  }
}
