package name.modid.helpers.tooltips;

import name.modid.helpers.modifiers.ModifierItemCaregory;

public class TooltipsHelper {
  public enum Icons {
    SOCKETED("gemstone_sockets_font"), INLINE("icons_font"), RARITY("rarity_font");

    private final String path;

    Icons(String path) {
      this.path = path;
    }

    public String getPath() {
      return path;
    }
  }

  private String tooltipCategory;

  public TooltipsHelper(ModifierItemCaregory itemType) {
    this.tooltipCategory = String.format("tooltip.gemstones.%s_type", itemType.toString().toLowerCase());
  }
}
