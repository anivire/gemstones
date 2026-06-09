package name.modid.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import name.modid.core.content.blocks.BlocksRegistry;
import net.minecraft.util.Identifier;

public class JewelryTableEmiRecipeCategory extends EmiRecipeCategory {
    public static final Identifier INSERT_ID = Identifier.of("gemstones", "jewelry_table_insert");
    public static final Identifier EXTRACT_ID = Identifier.of("gemstones", "jewelry_table_extract");
    public static final Identifier EXPAND_ID = Identifier.of("gemstones", "jewelry_table_expand");

    public static final JewelryTableEmiRecipeCategory INSERT = new JewelryTableEmiRecipeCategory(
            INSERT_ID, EmiStack.of(BlocksRegistry.JEWELRY_TABLE));
    public static final JewelryTableEmiRecipeCategory EXTRACT = new JewelryTableEmiRecipeCategory(
            EXTRACT_ID, EmiStack.of(BlocksRegistry.JEWELRY_TABLE));
    public static final JewelryTableEmiRecipeCategory EXPAND = new JewelryTableEmiRecipeCategory(
            EXPAND_ID, EmiStack.of(BlocksRegistry.JEWELRY_TABLE));

    public JewelryTableEmiRecipeCategory(Identifier id, EmiRenderable icon) {
        super(id, icon);
    }
}
