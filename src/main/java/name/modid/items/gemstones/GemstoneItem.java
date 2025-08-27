package name.modid.items.gemstones;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.tooltips.TooltipHelper;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

public class GemstoneItem extends Item {
  protected GemstoneType gemstoneType;
  protected GemstoneRarity rarityType;

  public GemstoneItem(Settings settings, GemstoneType gemstoneType, GemstoneRarity rarityType) {
    super(settings);

    this.gemstoneType = gemstoneType;
    this.rarityType = rarityType;
  }

  public GemstoneType getType() {
    return this.gemstoneType;
  }

  public GemstoneRarity getRarityType() {
    return this.rarityType;
  }

  @Override
  public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
    GemstoneItem gemstoneItem = (GemstoneItem) stack.getItem();
    GemstoneType gemstoneType = gemstoneItem.getType();
    Map<ModifierItemCategory, Map<GemstoneRarity, GemstoneModifier>> gemstoneModifiers = new LinkedHashMap<>(
        ModifierHelper.getGemstoneModifiers(gemstoneType, stack.getItem()));

    tooltip.add(TooltipHelper.getGemstoneRaritySprite(gemstoneItem.getRarityType()));
    tooltip.add(Text.empty());

    List<ModifierItemCategory> modifierOrder = Arrays.asList(ModifierItemCategory.MELEE,
        ModifierItemCategory.RANGED, ModifierItemCategory.TOOLS, ModifierItemCategory.ARMOR);

    gemstoneModifiers.entrySet().stream()
        .sorted(Comparator.comparingInt(entry -> modifierOrder.indexOf(entry.getKey())))
        .forEachOrdered(entry -> {
          Map<GemstoneRarity, GemstoneModifier> rarityMap = entry.getValue();
          GemstoneModifier modifier = rarityMap.get(gemstoneItem.getRarityType());

          if (modifier != null && gemstoneType != GemstoneType.LOCKED && gemstoneType != GemstoneType.EMPTY) {
            tooltip.add(modifier.getTooltipText(gemstoneItem.getRarityType(), true));
          }
        });
  }
}
