package name.modid.helpers.modifiers.category;

import java.util.ArrayList;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.instance.AbstractModifier;
import name.modid.helpers.modifiers.instance.LevelValues;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public class ModifierAreaEffect extends AbstractModifier {
  private final LevelValues radiusLevels;
  private final Integer duration;
  private final Integer amplifier;
  private final Boolean notMe;
  private final RegistryEntry<StatusEffect> effect;

  public ModifierAreaEffect(
      GemstoneType gemstoneType,
      GemstoneRarity rarityType,
      ModifierItemCategory itemCategory,
      ArrayList<Double> radiusLevels,
      Integer amplifier,
      Integer duration,
      Boolean notMe,
      RegistryEntry<StatusEffect> effect) {
    super(gemstoneType, itemCategory, rarityType);

    this.radiusLevels = new LevelValues(radiusLevels);
    this.duration = duration;
    this.amplifier = amplifier;
    this.notMe = notMe;
    this.effect = effect;
  }

  public LevelValues getRadiusLevels() {
    return radiusLevels;
  }

  public Integer getDuration() {
    return duration;
  }

  public Integer getAmplifier() {
    return amplifier;
  }

  public Boolean isNotMe() {
    return notMe;
  }

  public RegistryEntry<StatusEffect> getEffectEntry() {
    return effect;
  }
}
