package name.modid.core.api.tooltips;

import java.util.List;

import name.modid.core.api.modifiers.types.GemstoneQuality;
import net.minecraft.item.tooltip.TooltipData;

public record QualityTooltipData(List<GemstoneQuality> qualities) implements TooltipData {
}
