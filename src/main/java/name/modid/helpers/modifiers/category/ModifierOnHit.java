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

public class ModifierOnHit implements GemstoneModifier {
  public ArrayList<Double> eventChance = new ArrayList<Double>();
  public EventType eventType;
  public ModifierItemCategory itemType;
  public GemstoneType gemstoneType;
  public GemstoneRarity rarityType;

  public ModifierOnHit(ArrayList<Double> eventChance, EventType eventType,
      ModifierItemCategory itemType, GemstoneType gemstoneType) {
    this.eventChance = eventChance;
    this.itemType = itemType;
    this.gemstoneType = gemstoneType;
    this.eventType = eventType;
  }

  public MutableText getTooltipString(GemstoneRarity gemstoneRarityType,
      Boolean withCategoryString) {
    Object value = eventChance.get(gemstoneRarityType.getValue()) * 100;
    String tooltipCategoryType = withCategoryString
        ? String.format("tooltip.gemstones.%s_type", itemType.toString().toLowerCase())
        : "tooltip.gemstones.without_type";
    MutableText resultTooltip = Text.empty();
    MutableText eventString = Text.empty();

    if (this.eventType == EventType.LIGHTNING_BOLT) {
      eventString.append(Text.literal("Lighting Bolt").formatted(Formatting.YELLOW))
          .append(Text.literal("\uE007")
              .styled(style -> style.withFont(Identifier.of("gemstones", "icons_font")))
              .formatted(Formatting.WHITE));
    } else if (this.eventType == EventType.TORRENT) {
      eventString.append(Text.literal("Torrent").formatted(Formatting.BLUE))
          .append(Text.literal("\uE008")
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
            Text.literal(String.format("%.0f", value) + "%").formatted(Formatting.GREEN),
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
