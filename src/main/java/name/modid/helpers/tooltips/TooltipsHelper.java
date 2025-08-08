package name.modid.helpers.tooltips;

import name.modid.helpers.modifiers.GemstoneModifierItemType;

public class TooltipsHelper {
  private String tooltipCategory;

  public TooltipsHelper(GemstoneModifierItemType itemType) {
    this.tooltipCategory =
        String.format("tooltip.gemstones.%s_type", itemType.toString().toLowerCase());
  }

  // public MutableText getTooltip() {

  // }
}
