package name.modid.compat.rei;

import java.util.ArrayList;
import java.util.List;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import name.modid.core.content.blocks.BlocksRegistry;
import net.minecraft.text.Text;

public class JewelryTableReiCategory implements DisplayCategory<JewelryTableReiDisplay> {
  private static final int INPUT_Y = 22;
  private static final int OUTPUT_X = 118;
  private static final int OUTPUT_Y = 22;

  private final CategoryIdentifier<JewelryTableReiDisplay> category;
  private final Text title;

  public JewelryTableReiCategory(CategoryIdentifier<JewelryTableReiDisplay> category, Text title) {
    this.category = category;
    this.title = title;
  }

  @Override
  public CategoryIdentifier<? extends JewelryTableReiDisplay> getCategoryIdentifier() {
    return category;
  }

  @Override
  public Text getTitle() {
    return title;
  }

  @Override
  public Renderer getIcon() {
    return EntryStacks.of(BlocksRegistry.jewelryTable());
  }

  @Override
  public List<Widget> setupDisplay(JewelryTableReiDisplay display, Rectangle bounds) {
    List<Widget> widgets = new ArrayList<>();
    widgets.add(Widgets.createRecipeBase(bounds));

    int startX = bounds.x + 10;
    for (int i = 0; i < display.getInputEntries().size(); i++) {
      widgets.add(Widgets.createSlot(new Point(startX + i * 20, bounds.y + INPUT_Y))
          .entries(display.getInputEntries().get(i))
          .markInput());
    }

    widgets.add(Widgets.createArrow(new Point(bounds.x + 82, bounds.y + 23)));
    widgets.add(Widgets.createSlot(new Point(bounds.x + OUTPUT_X, bounds.y + OUTPUT_Y))
        .entries(display.getOutputEntries().get(0))
        .markOutput());

    if (display.isRiskyRemoval()) {
      widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width / 2, bounds.y + 49),
          Text.translatable("rei.gemstones.jewelry_table.remove.short_warning"))
          .centered()
          .noShadow()
          .color(0xFF5555));
    }

    return widgets;
  }

  @Override
  public int getDisplayHeight() {
    return 70;
  }
}
