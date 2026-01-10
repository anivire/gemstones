package name.modid.core.content.items.tools;

import java.util.List;
import java.util.random.RandomGenerator;

import name.modid.Gemstones;
import name.modid.core.api.tooltips.TooltipHelper.Icons;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class JewelryPliersItem extends Item implements ConsumableTool {
  private static final RandomGenerator RNG = RandomGenerator.getDefault();

  public JewelryPliersItem(Settings settings) {
    super(settings);
  }

  public boolean rollBreakGem() {
    return RNG.nextFloat() < 0.8f;
  }

  @Override
  public boolean isItemBarVisible(ItemStack stack) {
    return stack.isDamageable() && stack.getDamage() > 0;
  }

  @Override
  public void appendTooltip(ItemStack itemStack, TooltipContext context, List<Text> tooltip,
      TooltipType type) {
    tooltip.add(Text.translatable("item.gemstones.jewelry_pliers.info_1",
        Text.translatable("item.gemstones.jewelry_pliers.info_last").formatted(Formatting.GOLD)));

    MutableText iconInfo = Text.literal(InlineIcons.INFO.getSymbol())
        .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
        .formatted(Formatting.WHITE);

    tooltip.add(Text.empty());

    tooltip
        .add(iconInfo.append(Text.literal(" ").setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID)).append(
            Text.translatable("item.gemstones.jewelry_pliers.info_2", Text.literal("80%").formatted(Formatting.RED))
                .formatted(Formatting.GRAY))));
  }
}