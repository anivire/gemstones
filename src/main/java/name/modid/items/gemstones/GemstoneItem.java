package name.modid.items.gemstones;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.ModifierItemCaregory;
import name.modid.helpers.tooltips.GemstoneTooltipHelper;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;
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
    Map<ModifierItemCaregory, GemstoneModifier> gemstoneModifiers = new LinkedHashMap<>(
        ModifierHelper.getGemstoneModifiers(gemstoneType, stack.getItem()));

    tooltip.add(GemstoneTooltipHelper.getGemstoneRaritySprite(gemstoneItem.getRarityType()));
    tooltip.add(Text.empty());

    List<ModifierItemCaregory> modifierOrder = Arrays.asList(ModifierItemCaregory.MELEE,
        ModifierItemCaregory.RANGED, ModifierItemCaregory.TOOLS, ModifierItemCaregory.ARMOR);

    gemstoneModifiers.entrySet().stream()
        .sorted(Comparator.comparingInt(entry -> modifierOrder.indexOf(entry.getKey())))
        .forEachOrdered(entry -> {
          GemstoneModifier modifier = entry.getValue();

          if (gemstoneType != GemstoneType.LOCKED && gemstoneType != GemstoneType.EMPTY) {
            tooltip.add(modifier.getTooltipString(gemstoneItem.getRarityType(), true));
          }
        });
  }
}
