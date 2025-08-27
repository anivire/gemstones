package name.modid.helpers.modifiers.category;

import java.util.ArrayList;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.instance.AbstractModifier;
import name.modid.helpers.modifiers.instance.LevelValues;
import name.modid.helpers.modifiers.type.EventType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;

public class ModifierOnDamage extends AbstractModifier {
  private final LevelValues values;
  private final LevelValues additionalValues;
  private final EventType eventType;

  public ModifierOnDamage(
      GemstoneType gemstoneType,
      GemstoneRarity rarityType,
      ModifierItemCategory itemCategory,
      ArrayList<Double> values,
      ArrayList<Double> additionalValues,
      EventType eventType) {
    super(gemstoneType, itemCategory, rarityType);

    this.values = new LevelValues(values);
    this.additionalValues = new LevelValues(additionalValues);
    this.eventType = eventType;
  }

  public LevelValues getValues() {
    return values;
  }

  public LevelValues getAdditionalValues() {
    return additionalValues;
  }

  public EventType getEventType() {
    return eventType;
  }
}