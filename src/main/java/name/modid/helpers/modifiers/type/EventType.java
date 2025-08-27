package name.modid.helpers.modifiers.type;

public enum EventType {
  LIGHTNING_BOLT("Lighting Bolt"), EXTRA_HEALTH("Extra Health"), TORRENT("Torrent"),
  ADDITIONAL_DAMAGE("Bonus Damage"), INCREASE_GEODES_DROP("Loot Geodes"),
  INCREASE_MOSSY_BOX_DROP("Loot Mossy Box"), POTION_DURATION("Potion Duration");

  private final String name;

  EventType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
};
