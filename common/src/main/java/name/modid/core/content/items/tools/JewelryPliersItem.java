package name.modid.core.content.items.tools;

import java.util.List;

import name.modid.core.api.tooltips.TooltipText;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class JewelryPliersItem extends Item implements ConsumableTool {
  public JewelryPliersItem(Settings settings) {
    super(settings);
  }

  @Override
  public boolean isItemBarVisible(ItemStack stack) {
    return stack.isDamageable() && stack.getDamage() > 0;
  }

  @Override
  public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip,
      TooltipType type) {
    tooltip.add(Text.translatable("item.gemstones.jewelry_pliers.info_1",
        Text.translatable("item.gemstones.jewelry_pliers.info_last").formatted(Formatting.GOLD)));

    tooltip.add(Text.empty());
    tooltip.add(TooltipText.info(Text.literal(" ").append(
        Text.translatable("item.gemstones.jewelry_pliers.info_2", Text.literal("80%").formatted(Formatting.RED))
            .formatted(Formatting.GRAY))));
  }
}