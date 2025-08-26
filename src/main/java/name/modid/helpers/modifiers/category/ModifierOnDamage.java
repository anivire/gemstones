package name.modid.helpers.modifiers.category;

import java.util.ArrayList;

import name.modid.Gemstones;
import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.type.EventType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModifierOnDamage implements GemstoneModifier {
  public ArrayList<Double> value = new ArrayList<Double>();
  public ArrayList<Double> additionalValue = new ArrayList<Double>();
  public EventType eventType;
  public ModifierItemCategory itemType;
  public GemstoneType gemstoneType;
  public GemstoneRarity rarityType;

  public ModifierOnDamage(ArrayList<Double> value, ArrayList<Double> additionalValue,
      EventType eventType, ModifierItemCategory itemType, GemstoneType gemstoneType) {
    this.value = value;
    this.additionalValue = new ArrayList<Double>(additionalValue);
    this.itemType = itemType;
    this.gemstoneType = gemstoneType;
    this.eventType = eventType;
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

    MutableText icon = Text.literal("\uE006")
        .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "icons_font")))
        .formatted(Formatting.GREEN);

    return resultTooltip.append(Text.translatable(tooltipCategoryType).formatted(Formatting.GRAY))
        .append(Text.translatable(
            String.format("tooltip.gemstones.%s.%s_bonus", gemstoneType.toString().toLowerCase(),
                itemType.toString().toLowerCase()),
            icon.append(Text.literal(String.format("%.0f", v) + "%").formatted(Formatting.GREEN)
                .styled(style -> style.withFont(Identifier.of("minecraft", "default")))),
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
