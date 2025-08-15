package name.modid.helpers.modifiers.modifierTypes;

import java.util.ArrayList;
import name.modid.Gemstones;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierItemCaregory;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModifierCustomCondition implements GemstoneModifier {
  public ArrayList<Double> value = new ArrayList<Double>();
  public ArrayList<Double> additionalValue = new ArrayList<Double>();
  public ConditionType conditionType;
  public ModifierItemCaregory itemType;
  public GemstoneType gemstoneType;
  public GemstoneRarity rarityType;

  public ModifierCustomCondition(ArrayList<Double> value, ArrayList<Double> additionalValue,
      ConditionType conditionType, ModifierItemCaregory itemType, GemstoneType gemstoneType) {
    this.value = value;
    this.additionalValue = new ArrayList<Double>(additionalValue);
    this.itemType = itemType;
    this.gemstoneType = gemstoneType;
    this.conditionType = conditionType;
  }

  public MutableText getTooltipString(GemstoneRarity gemstoneRarityType,
      Boolean withCategoryString) {
    Object v = value.get(gemstoneRarityType.getValue()) * 100;
    String tooltipCategoryType = withCategoryString
        ? String.format("tooltip.gemstones.%s_type", itemType.toString().toLowerCase())
        : "tooltip.gemstones.without_type";
    MutableText resultTooltip = Text.empty();
    MutableText eventString = Text.empty();

    if (this.conditionType == ConditionType.ENTITY_IN_WATER) {
      eventString.append(Text.literal("Water").formatted(Formatting.BLUE))
          .append(Text.literal("\uE005")
              .styled(style -> style.withFont(Identifier.of("gemstones", "gemstone_sprite_icons")))
              .formatted(Formatting.WHITE));
    } else if (this.conditionType == ConditionType.PLAYER_IN_WATER) {
      eventString.append(Text.literal("Water").formatted(Formatting.BLUE))
          .append(Text.literal("\uE005")
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
}
