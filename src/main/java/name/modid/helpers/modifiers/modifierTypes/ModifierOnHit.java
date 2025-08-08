package name.modid.helpers.modifiers.modifierTypes;

import java.util.ArrayList;
import name.modid.Gemstones;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.GemstoneModifierItemType;
import name.modid.helpers.types.GemstoneRarityType;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModifierOnHit implements GemstoneModifier {
  public ArrayList<Double> eventChance = new ArrayList<Double>();
  public EventType eventType;
  public GemstoneModifierItemType itemType;
  public GemstoneType gemstoneType;
  public GemstoneRarityType rarityType;

  public ModifierOnHit(ArrayList<Double> eventChance, EventType eventType,
      GemstoneModifierItemType itemType, GemstoneType gemstoneType) {
    this.eventChance = eventChance;
    this.itemType = itemType;
    this.gemstoneType = gemstoneType;
    this.eventType = eventType;
  }

  public MutableText getTooltipString(GemstoneRarityType gemstoneRarityType,
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
              .styled(style -> style.withFont(Identifier.of("gemstones", "gemstone_sprite_icons")))
              .formatted(Formatting.WHITE));
    } else if (this.eventType == EventType.TORRENT) {
      eventString.append(Text.literal("Torrent").formatted(Formatting.BLUE))
          .append(Text.literal("\uE008")
              .styled(style -> style.withFont(Identifier.of("gemstones", "gemstone_sprite_icons")))
              .formatted(Formatting.WHITE));
    }

    return resultTooltip.append(Text.translatable(tooltipCategoryType).formatted(Formatting.GRAY))
        .append(Text.literal("\uE006").styled(
            style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "gemstone_sprite_icons"))))
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

  public GemstoneRarityType getRarityType() {
    return this.rarityType;
  }

  public void setRarityType(GemstoneRarityType rarityType) {
    this.rarityType = rarityType;
  }
}
