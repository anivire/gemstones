package name.modid.core.api.modifiers.impl.categories;

import java.util.ArrayList;

import name.modid.core.api.modifiers.EventType;
import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.LevelValues;
import name.modid.core.api.modifiers.ModifierItemCategory;
import name.modid.core.api.modifiers.impl.AbstractModifier;

public class ModifierOnFirstHitMelee extends AbstractModifier {
  private final LevelValues values;
  private final EventType eventType;

  public ModifierOnFirstHitMelee(
      GemstoneType gemstoneType,
      GemstoneQuality rarityType,
      ModifierItemCategory itemCategory,
      ArrayList<Double> values,
      EventType eventType) {
    super(gemstoneType, itemCategory, rarityType);

    this.values = new LevelValues(values);
    this.eventType = eventType;
  }

  public LevelValues getValues() {
    return values;
  }

  public EventType getEventType() {
    return eventType;
  }
}
