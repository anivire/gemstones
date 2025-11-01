package name.modid.core.content.items.tools;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ChiselItem extends Item implements ConsumableTool {
  public ChiselItem(Settings settings) {
    super(settings);
  }

  @Override
  public boolean isItemBarVisible(ItemStack stack) {
    return stack.isDamageable() && stack.getDamage() > 0;
  }
}