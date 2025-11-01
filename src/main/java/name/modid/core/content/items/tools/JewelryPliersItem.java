package name.modid.core.content.items.tools;

import java.util.random.RandomGenerator;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class JewelryPliersItem extends Item implements ConsumableTool {
  private static final RandomGenerator RNG = RandomGenerator.getDefault();

  public JewelryPliersItem(Settings settings) {
    super(settings);
  }

  public boolean rollBreakGem() {
    return RNG.nextFloat() < 0.8f;
  }

  @Override
  public boolean isItemBarVisible(ItemStack stack) {
    return stack.isDamageable() && stack.getDamage() > 0;
  }
}