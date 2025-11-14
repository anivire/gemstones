package name.modid.core.content.items.tools;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ConsumableTool {

  default void damageOne(PlayerEntity player, ItemStack stack) {
    stack.damage(1, player, EquipmentSlot.MAINHAND);
  }

  default boolean hasDurability(ItemStack stack) {
    return stack.getMaxDamage() > 0 && stack.getDamage() < stack.getMaxDamage();
  }
}