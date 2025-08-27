package name.modid.helpers.modifiers.category;

import java.util.ArrayList;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.instance.AbstractModifier;
import name.modid.helpers.modifiers.instance.LevelValues;
import name.modid.helpers.modifiers.type.EventType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;

public class ModifierCustomCondition extends AbstractModifier {
  private final LevelValues value;
  private final LevelValues additionalValue;
  private final EventType eventType;

  public ModifierCustomCondition(
      GemstoneType gemstoneType,
      GemstoneRarity rarityType,
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