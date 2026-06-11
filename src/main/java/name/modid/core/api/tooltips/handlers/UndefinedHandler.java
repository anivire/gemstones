package name.modid.core.api.tooltips.handlers;

import name.modid.core.api.tooltips.TooltipHelper;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class UndefinedHandler implements TooltipHandler {
  @Override
  public MutableText buildTooltip() {
    return TooltipHelper.safeTranslatable("tooltip.gemstones.bonus.undefined").formatted(Formatting.RED);
  }
}
