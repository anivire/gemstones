package name.modid.helpers.modifiers.category;

import java.util.ArrayList;

import name.modid.Gemstones;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.type.EventType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModifierOnBlockBreak implements GemstoneModifier {
  public ArrayList<Double> value = new ArrayList<Double>();
  public ArrayList<Double> additionalValue = new ArrayList<Double>();
  public ModifierItemCategory itemType;
  public EventType eventType;
  public GemstoneType gemstoneType;
  public GemstoneRarity rarityType;

  public ModifierOnBlockBreak(ArrayList<Double> value, ArrayList<Double> additionalValue,
      ModifierItemCategory itemType, EventType eventType, GemstoneType gemstoneType) {
    this.value = new ArrayList<Double>(value);
    this.additionalValue = new ArrayList<Double>(additionalValue);
    this.itemType = itemType;
    this.eventType = eventType;
    this.gemstoneType = gemstoneType;
  }

  public MutableText getTooltipString(GemstoneRarity gemstoneRarityType,
      Boolean withCategoryString) {
    Object v = value.get(gemstoneRarityType.getValue()) * 100;
    String tooltipCategoryType = withCategoryString
        ? String.format("tooltip.gemstones.%s_type", itemType.toString().toLowerCase())
        : "tooltip.gemstones.without_type";
    MutableText resultTooltip = Text.empty();
    MutableText eventString = Text.empty();

    if (this.eventType == EventType.EXTRA_HEALTH) {
      eventString.append(Text.literal("extra 1").formatted(Formatting.YELLOW))
          .append(Text.literal("\uE004")
              .styled(style -> style.withFont(Identifier.of("gemstones", "icons_font")))
              .formatted(Formatting.WHITE));
    }

    return resultTooltip.append(Text.translatable(tooltipCategoryType).formatted(Formatting.GRAY))
        .append(Text.literal("\uE006").styled(
            style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "icons_font"))))
        .formatted(Formatting.GREEN)
        .append(Text.translatable(
            String.format("tooltip.gemstones.%s.%s_bonus", gemstoneType.toString().toLowerCase(),
                itemType.toString().toLowerCase()),
            Text.literal(String.format("%.0f", v) + "%").formatted(Formatting.GREEN), eventString)
            .formatted(Formatting.GOLD));
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
