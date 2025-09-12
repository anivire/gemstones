package name.modid.core.api.modifiers.impl.categories;

import java.util.ArrayList;

import name.modid.core.api.modifiers.EventType;
import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.LevelValues;
import name.modid.core.api.modifiers.ModifierItemCategory;
import name.modid.core.api.modifiers.impl.AbstractModifier;

public class ModifierOnHitProjectile extends AbstractModifier {
  private final LevelValues eventChances;
  private final EventType eventType;

  public ModifierOnHitProjectile(
      GemstoneType gemstoneType,
      GemstoneQuality rarityType,
      ModifierItemCategory itemCategory,
      ArrayList<Double> eventChances,
      EventType eventType) {
    super(gemstoneType, itemCategory, rarityType);

    this.eventChances = new LevelValues(eventChances);
    this.eventType = eventType;
  }

  public LevelValues getEventChances() {
    return eventChances;
  }

  public EventType getEventType() {
    return eventType;
  }
}