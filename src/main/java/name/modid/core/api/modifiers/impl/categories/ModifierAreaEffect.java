package name.modid.core.api.modifiers.impl.categories;

import java.util.ArrayList;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.LevelValues;
import name.modid.core.api.modifiers.ModifierItemCategory;
import name.modid.core.api.modifiers.impl.AbstractModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public class ModifierAreaEffect extends AbstractModifier {
  private final LevelValues radiusLevels;
  private final Integer duration;
  private final Integer amplifier;
  private final Boolean notMe;
  private final Boolean onlyPlayers;
  private final RegistryEntry<StatusEffect> effect;

  public ModifierAreaEffect(
      GemstoneType gemstoneType,
      GemstoneQuality rarityType,
      ModifierItemCategory itemCategory,
      ArrayList<Double> radiusLevels,
      Integer amplifier,
      Integer duration,
      Boolean notMe,
      Boolean onlyPlayers,
      RegistryEntry<StatusEffect> effect) {
    super(gemstoneType, itemCategory, rarityType);

    this.radiusLevels = new LevelValues(radiusLevels);
    this.duration = duration;
    this.amplifier = amplifier;
    this.notMe = notMe;
    this.onlyPlayers = onlyPlayers;
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

  public Boolean isOnlyPlayers() {
    return onlyPlayers;
  }

  public RegistryEntry<StatusEffect> getEffectEntry() {
    return effect;
  }
}
