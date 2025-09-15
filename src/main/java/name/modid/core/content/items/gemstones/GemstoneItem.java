package name.modid.core.content.items.gemstones;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.helpers.ModifierHelper;
import name.modid.core.api.modifiers.impl.GemstoneModifier;
import name.modid.core.api.modifiers.impl.ModifierItemCategory;
import name.modid.core.api.modifiers.tooltips.TooltipHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GemstoneItem extends Item {
  protected GemstoneType gemstoneType;
  protected GemstoneQuality rarityType;

  public GemstoneItem(Settings settings, GemstoneType gemstoneType, GemstoneQuality rarityType) {
    super(settings);
    this.gemstoneType = gemstoneType;
    this.rarityType = rarityType;
  }

  public GemstoneType getType() {
    return this.gemstoneType;
  }

  public GemstoneQuality getRarityType() {
    return this.rarityType;
  }

  @Override
  public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
    GemstoneItem gemstoneItem = (GemstoneItem) stack.getItem();
    GemstoneType gemstoneType = gemstoneItem.getType();
    Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> gemstoneModifiers = new LinkedHashMap<>(
        ModifierHelper.getGemstoneModifiers(gemstoneType, stack.getItem()));

    tooltip.add(TooltipHelper.getGemstoneQualitySprite(gemstoneItem.getRarityType()));

    if (!gemstoneModifiers.isEmpty()) {
      tooltip.add(Text.empty());
      tooltip.add(Text.translatable("tooltip.gemstones.gemstone_bonus").formatted(Formatting.GRAY));
    }

    List<ModifierItemCategory> modifierOrder = Arrays.asList(
        ModifierItemCategory.MELEE,
        ModifierItemCategory.RANGED,
        ModifierItemCategory.TOOLS,
        ModifierItemCategory.ARMOR);

    gemstoneModifiers.entrySet().stream()
        .sorted(Comparator.comparingInt(entry -> modifierOrder.indexOf(entry.getKey())))
        .forEachOrdered(entry -> {
          Map<GemstoneQuality, GemstoneModifier> rarityMap = entry.getValue();
          GemstoneModifier modifier = rarityMap.get(gemstoneItem.getRarityType());

          if (modifier != null && gemstoneType != GemstoneType.LOCKED && gemstoneType != GemstoneType.EMPTY) {
            tooltip.add(modifier.getTooltipText(gemstoneItem.getRarityType(), true));
          }
        });
  }
}