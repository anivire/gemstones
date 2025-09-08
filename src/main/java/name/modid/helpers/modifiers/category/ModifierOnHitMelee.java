package name.modid.helpers.modifiers.category;

import java.util.ArrayList;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.instance.AbstractModifier;
import name.modid.helpers.modifiers.instance.LevelValues;
import name.modid.helpers.modifiers.type.EventType;
import name.modid.helpers.modifiers.type.ModifierItemCategory;

public class ModifierOnHitMelee extends AbstractModifier {
  private final LevelValues eventChances;
  private final EventType eventType;

  public ModifierOnHitMelee(
      GemstoneType gemstoneType,
      GemstoneRarity rarityType,
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