package name.modid.core.content.items.tools;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

public class JewelryHammerItem extends Item implements ConsumableTool {
  public JewelryHammerItem(Settings settings) {
    super(settings);
  }

  @Override
  public boolean isItemBarVisible(ItemStack stack) {
    return stack.isDamageable() && stack.getDamage() > 0;
  }

  @Override
  public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip,
      TooltipType type) {
    tooltip.add(Text.translatable("item.gemstones.jewelry_hammer.info"));
  }
}
