package name.modid.helpers.modifiers.category;

import java.util.ArrayList;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.instance.AbstractModifier;
import name.modid.helpers.modifiers.instance.LevelValues;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public class ModifierOnHitEffectProjectile extends AbstractModifier {
  private final LevelValues inflitChanceValues;
  private final RegistryEntry<StatusEffect> effectEntry;
  private final Integer duration;
  private final Integer amplifier;
  private final Integer maxStackCount;
  private final Boolean isStacking;

  public ModifierOnHitEffectProjectile(
      GemstoneType gemstoneType,
      GemstoneRarity rarityType,
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

  public Boolean isStacking() {
    return isStacking;
  }
}