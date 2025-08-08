package name.modid.helpers.modifiers.modifierTypes;

import java.util.ArrayList;
import name.modid.Gemstones;
import name.modid.entities.EffectRegistrationHelper;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.GemstoneModifierItemType;
import name.modid.helpers.types.GemstoneRarityType;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModifierOnHitEffect implements GemstoneModifier {
  public ArrayList<Double> inflitChance = new ArrayList<Double>();
  public GemstoneModifierItemType itemType;
  public int duration;
  public int amplifier;
  public RegistryEntry<StatusEffect> effect;
  public boolean isStacking;
  public int maxStackCount;
  public GemstoneType gemstoneType;
  public GemstoneRarityType rarityType;

  public ModifierOnHitEffect(ArrayList<Double> inflitChance, int duration, int amplifier,
      GemstoneModifierItemType itemType, RegistryEntry<StatusEffect> effect, boolean isStacking,
      int maxStackCount, GemstoneType gemstoneType) {
    this.inflitChance = inflitChance;
    this.duration = duration;
    this.amplifier = amplifier;
    this.itemType = itemType;
    this.effect = effect;
    this.gemstoneType = gemstoneType;
    this.maxStackCount = maxStackCount;
    this.isStacking = isStacking;
  }

  public MutableText getTooltipString(GemstoneRarityType gemstoneRarityType,
      Boolean withCategoryString) {
    Object value = inflitChance.get(gemstoneRarityType.getValue()) * 100;
    String tooltipCategoryType = withCategoryString
        ? String.format("tooltip.gemstones.%s_type", itemType.toString().toLowerCase())
        : "tooltip.gemstones.without_type";
    MutableText effectString = Text.empty();
    MutableText resultTooltip = Text.empty();

    if (this.effect == EffectRegistrationHelper.BLEEDING_EFFECT) {
      effectString.append(Text.literal("Bleeding").formatted(Formatting.RED))
          .append(Text.literal("\uE002").styled(
              style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "gemstone_sprite_icons"))))
          .formatted(Formatting.WHITE);
    } else if (this.effect == EffectRegistrationHelper.GUARDIAN_SMITE_EFFECT) {
      effectString.append(Text.literal("Guardian Smite").formatted(Formatting.RED))
          .append(Text.literal("\uE003").styled(
              style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "gemstone_sprite_icons"))))
          .formatted(Formatting.WHITE);
    } else if (this.effect == EffectRegistrationHelper.QUICK_SANDS_EFFECT) {
      effectString.append(Text.literal("Quick Sands").formatted(Formatting.RED));
    } else {
      effectString.append(Text.literal(this.effect.toString()).formatted(Formatting.WHITE));
    }

    return resultTooltip.append(Text.translatable(tooltipCategoryType).formatted(Formatting.GRAY))
        .append(Text.literal("\uE006").styled(
            style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "gemstone_sprite_icons"))))
        .formatted(Formatting.GREEN)
        .append(Text.translatable(
            String.format("tooltip.gemstones.%s.%s_bonus", gemstoneType.toString().toLowerCase(),
                itemType.toString().toLowerCase()),
            Text.literal(String.format("%.0f", value) + "%").formatted(Formatting.GREEN),
            effectString).formatted(Formatting.GOLD));
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
