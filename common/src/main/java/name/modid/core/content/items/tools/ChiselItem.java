package name.modid.core.content.items.tools;

import java.util.List;

import name.modid.core.api.tooltips.TooltipText;
import name.modid.core.content.items.registries.ItemsRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChiselItem extends Item implements ConsumableTool {
  public ChiselItem(Settings settings) {
    super(settings);
  }

  @Override
  public boolean isItemBarVisible(ItemStack stack) {
    return stack.isDamageable() && stack.getDamage() > 0;
  }

  @Override
  public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip,
      TooltipType type) {
    ItemStack crystal = ItemsRegistry.expansionCrystal().getDefaultStack();

    tooltip.add(Text.translatable("item.gemstones.chisel.info_1",
        Text.translatable("item.gemstones.chisel.info_add").formatted(Formatting.GOLD)));

    tooltip.add(Text.empty());
    tooltip.add(TooltipText.info(Text.literal(" ").append(
        Text.translatable("item.gemstones.chisel.info_2", Text.empty().append(crystal.toHoverableText()))
            .formatted(Formatting.GRAY))));
  }
}
