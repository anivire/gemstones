package name.modid.compat.rei;

import java.util.List;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

public class JewelryTableReiDisplay extends BasicDisplay {
  private final CategoryIdentifier<JewelryTableReiDisplay> category;
  private final boolean riskyExtraction;

  public JewelryTableReiDisplay(
      CategoryIdentifier<JewelryTableReiDisplay> category,
      List<EntryIngredient> inputs,
      List<EntryIngredient> outputs,
      boolean riskyExtraction) {
    super(inputs, outputs);
    this.category = category;
    this.riskyExtraction = riskyExtraction;
  }

  @Override
  public CategoryIdentifier<?> getCategoryIdentifier() {
    return category;
  }

  public boolean isRiskyExtraction() {
    return riskyExtraction;
  }
}
