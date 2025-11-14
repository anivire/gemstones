package name.modid.core.content.screen.slots;

import name.modid.core.content.screen.JewelryTableScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

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

    if (handler.getCurrentMode() == JewelryTableScreenHandler.ActionMode.EXTRACT) {
      if (handler.wasLastGemBroken()) {
        taken.setCount(0);
        playGemBrokenSound(player);
      }
    }

    handler.finalizeTakeResult(player);
    handler.updateOutputs();
    handler.sendContentUpdates();

    if (player.getWorld().isClient && !handler.wasLastGemBroken())
      return;

    var world = player.getWorld();
    var pos = player.getBlockPos();

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

  private void playGemBrokenSound(PlayerEntity player) {
    if (player.getWorld().isClient)
      return;

    var world = player.getWorld();
    var pos = player.getBlockPos();

    world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1f, 0.7f);
    world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.7f, 0.6f);
  }
}