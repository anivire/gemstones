package name.modid.compat.emi;

import java.util.List;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.api.widget.FillingArrowWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class JewelryTableEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final EmiRecipeCategory category;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    private final boolean riskyExtraction;

    public JewelryTableEmiRecipe(Identifier id, EmiRecipeCategory category,
            List<EmiIngredient> inputs, List<EmiStack> outputs, boolean riskyExtraction) {
        this.id = id;
        this.category = category;
        this.inputs = inputs;
        this.outputs = outputs;
        this.riskyExtraction = riskyExtraction;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 120;
    }

    @Override
    public int getDisplayHeight() {
        return riskyExtraction ? 52 : 36;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < inputs.size(); i++) {
            widgets.add(new SlotWidget(inputs.get(i), i * 18, 9));
        }
        int arrowX = inputs.size() * 18 + 4;
        widgets.add(new FillingArrowWidget(arrowX, 10, 5000));
        int outputX = arrowX + 28;
        widgets.add(new SlotWidget(outputs.get(0), outputX, 5).large(true).recipeContext(this));

        if (riskyExtraction) {
            widgets.addText(
                    Text.translatable("emi.gemstones.jewelry_table.extract.short_warning"),
                    getDisplayWidth() / 2, 38, 0xFF5555, false)
                    .horizontalAlign(TextWidget.Alignment.CENTER);
        }
    }
}
