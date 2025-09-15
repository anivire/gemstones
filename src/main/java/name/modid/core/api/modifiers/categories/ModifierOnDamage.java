package name.modid.core.api.modifiers.categories;

import java.util.ArrayList;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.LevelValues;
import name.modid.core.api.modifiers.impl.EventType;
import name.modid.core.api.modifiers.impl.ModifierItemCategory;

public class ModifierOnDamage extends AbstractModifier {
  private final LevelValues values;
  private final LevelValues additionalValues;
  private final EventType eventType;

  public ModifierOnDamage(
      GemstoneType gemstoneType,
      GemstoneQuality rarityType,
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