package name.modid.core.api.tooltips.handlers;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class UndefinedHandler implements TooltipHandler {
  @Override
  public MutableText buildTooltip() {
    return Text.literal("Undefined bonus").formatted(Formatting.RED);
  }
}