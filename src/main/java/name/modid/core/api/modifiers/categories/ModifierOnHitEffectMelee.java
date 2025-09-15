package name.modid.core.api.modifiers.categories;

import java.util.ArrayList;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.LevelValues;
import name.modid.core.api.modifiers.impl.ModifierItemCategory;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public class ModifierOnHitEffectMelee extends AbstractModifier {
  public LevelValues inflitChanceValues;
  public RegistryEntry<StatusEffect> effectEntry;
  public Integer duration;
  public Integer amplifier;
  public Integer maxStackCount;
  public Boolean isStacking;

  public ModifierOnHitEffectMelee(
      GemstoneType gemstoneType,
      GemstoneQuality rarityType,
      ModifierItemCategory itemCategory,
      ArrayList<Double> inflitChanceValues,
      RegistryEntry<StatusEffect> effectEntry,
      Integer duration,
      Integer amplifier,
      Integer maxStackCount,
      Boolean isStacking) {
    super(gemstoneType, itemCategory, rarityType);

    this.inflitChanceValues = new LevelValues(inflitChanceValues);
    this.effectEntry = effectEntry;
    this.duration = duration;
    this.amplifier = amplifier;
    this.maxStackCount = maxStackCount;
    this.isStacking = isStacking;
  }

  public LevelValues getInflitChanceValues() {
    return inflitChanceValues;
  }

  public RegistryEntry<StatusEffect> getEffectEntry() {
    return effectEntry;
  }

  public Integer getDuration() {
    return duration;
  }

  public Integer getAmplifier() {
    return amplifier;
  }

  public Integer getMaxStackCount() {
    return maxStackCount;
  }

  public Boolean getIsStacking() {
    return isStacking;
  }
}
