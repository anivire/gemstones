package name.modid.helpers.modifiers.type;

public enum EventType {
  LIGHTNING_BOLT("Lighting Bolt"),
  EXTRA_HEALTH("Extra Health"),
  TORRENT("Torrent"),
  INCREASE_GEODES_DROP("Geodes"),
  INCREASE_MOSSY_BOX_DROP("Mossy Box"),
  INCREASE_MOB_SPAWNRATE("Mobs Spawnrate"),
  ADDITIONAL_GOLD_DROP("Gold"),
  POTION_DURATION("Potion Duration"),
  REGENERATE_BLOCK("Regenerate Block"),
  COPY_ENTITY_DROP("Mob Loot"),
  ADDITIONAL_DAMAGE("Bonus Damage");

  private final String name;

  EventType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
};
