package name.modid.core.api.modifiers.impl.categories;

import java.util.ArrayList;

import name.modid.core.api.modifiers.EventType;
import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.LevelValues;
import name.modid.core.api.modifiers.ModifierItemCategory;
import name.modid.core.api.modifiers.impl.AbstractModifier;

public class ModifierCustomCondition extends AbstractModifier {
  private final LevelValues value;
  private final LevelValues additionalValue;
  private final EventType eventType;

  public ModifierCustomCondition(
      GemstoneType gemstoneType,
      GemstoneQuality rarityType,
      ModifierItemCategory itemCategory,
      ArrayList<Double> value,
      ArrayList<Double> additionalValue,
      EventType eventType) {
    super(gemstoneType, itemCategory, rarityType);

    this.value = new LevelValues(value);
    this.additionalValue = new LevelValues(additionalValue);
    this.eventType = eventType;
  }

  public LevelValues getValues() {
    return value;
  }

  public LevelValues getAdditionalValue() {
    return additionalValue;
  }

  public EventType getEventType() {
    return eventType;
  }
}