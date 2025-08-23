package name.modid.helpers.modifiers.category;

import java.util.ArrayList;

import name.modid.Gemstones;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.tooltips.GemstoneTooltipHelper.Icons;
import name.modid.helpers.modifiers.type.EventType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModifierOnFirstHit implements GemstoneModifier {
  public ArrayList<Double> values = new ArrayList<Double>();
  public EventType eventType;
  public ModifierItemCategory itemType;
  public GemstoneType gemstoneType;
  public GemstoneRarity rarityType;

  public ModifierOnFirstHit(ArrayList<Double> values, EventType eventType,
      ModifierItemCategory itemType, GemstoneType gemstoneType) {
    this.values = values;
    this.itemType = itemType;
    this.gemstoneType = gemstoneType;
    this.eventType = eventType;
  }

  public MutableText getTooltipString(GemstoneRarity gemstoneRarityType,
      Boolean withCategoryString) {
    Object value = values.get(gemstoneRarityType.getValue()) * 100;
    String tooltipCategoryType = withCategoryString
        ? String.format("tooltip.gemstones.%s_type", itemType.toString().toLowerCase())
        : "tooltip.gemstones.without_type";
    MutableText resultTooltip = Text.empty();
    MutableText eventString = Text.empty();

    return resultTooltip.append(Text.translatable(tooltipCategoryType).formatted(Formatting.GRAY))
        .formatted(Formatting.GREEN)
        .append(Text.translatable(
            String.format("tooltip.gemstones.%s.%s_bonus", gemstoneType.toString().toLowerCase(),
                itemType.toString().toLowerCase()),
            Text.empty().append(Text.literal("\uE006").styled(
                style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
                .formatted(Formatting.GREEN))
                .append(Text.literal(String.format("%.0f", value) + "%").formatted(Formatting.GREEN)),
            eventString).formatted(Formatting.GOLD));
  }

  public GemstoneType getGemstoneType() {
    return this.gemstoneType;
  }

  public GemstoneRarity getRarityType() {
    return this.rarityType;
  }

  public void setRarityType(GemstoneRarity rarityType) {
    this.rarityType = rarityType;
  }

  public ModifierItemCategory getItemCategory() {
    return this.itemType;
  };
}
