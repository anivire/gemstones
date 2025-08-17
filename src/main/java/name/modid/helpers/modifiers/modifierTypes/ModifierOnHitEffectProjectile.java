package name.modid.helpers.modifiers.modifierTypes;

import java.util.ArrayList;

import name.modid.Gemstones;
import name.modid.effects.EffectRegistrationHelper;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierItemCaregory;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModifierOnHitEffectProjectile implements GemstoneModifier {
  public ArrayList<Double> inflitChance = new ArrayList<Double>();
  public ModifierItemCaregory itemType;
  public int duration;
  public int amplifier;
  public RegistryEntry<StatusEffect> effect;
  public boolean isStacking;
  public int maxStackCount;
  public GemstoneType gemstoneType;
  public GemstoneRarity rarityType;

  public ModifierOnHitEffectProjectile(ArrayList<Double> inflitChance, int duration, int amplifier,
      ModifierItemCaregory itemType, RegistryEntry<StatusEffect> effect, boolean isStacking,
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

  public MutableText getTooltipString(GemstoneRarity gemstoneRarityType,
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
    } else if (this.effect == EffectRegistrationHelper.HARVEST_MARK_EFFECT) {
      effectString.append(Text.literal("Harvest Mark").formatted(Formatting.GREEN))
          .append(Text.literal("\uE009").styled(
              style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "gemstone_sprite_icons"))))
          .formatted(Formatting.WHITE);
    } else if (this.effect == StatusEffects.SLOWNESS) {
      effectString.append(Text.literal("Slowness").formatted(Formatting.RED))
          .append(Text.literal("\uE010").styled(
              style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "gemstone_sprite_icons"))))
          .formatted(Formatting.WHITE);
    } else if (this.effect == EffectRegistrationHelper.STUNNED_EFFECT) {
      effectString.append(Text.literal("Stunned").formatted(Formatting.RED))
          .append(Text.literal("\uE011").styled(
              style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "gemstone_sprite_icons"))))
          .formatted(Formatting.WHITE);
    } else {
      StatusEffect e = this.effect.value();
      effectString.append(e.getName()).formatted(e.isBeneficial() ? Formatting.GREEN : Formatting.RED);
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

  public GemstoneRarity getRarityType() {
    return this.rarityType;
  }

  public void setRarityType(GemstoneRarity rarityType) {
    this.rarityType = rarityType;
  }
}
