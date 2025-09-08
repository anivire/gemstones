package name.modid.items.geodes;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

public record GemstoneListTooltipData(List<ItemStack> stacks) implements TooltipData {
}