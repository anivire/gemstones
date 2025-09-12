package name.modid.core.api.modifiers;

public enum EventType {
  LIGHTNING_BOLT("tooltip.gemstones.event_name.lighting_bolt"),
  EXTRA_HEALTH("tooltip.gemstones.event_name.extra_health"),
  HEAL("tooltip.gemstones.event_name.heal"),
  LIFE_STEAL("tooltip.gemstones.event_name.lifesteal"),
  TORRENT("tooltip.gemstones.event_name.torrent"),
  INCREASE_GEODES_DROP("tooltip.gemstones.event_name.increase_geodes_drop"),
  INCREASE_MOSSY_BOX_DROP("tooltip.gemstones.event_name.increase_mossy_box_drop"),
  INCREASE_MOB_SPAWNRATE("tooltip.gemstones.event_name.increase_mob_spawnrate"),
  ADDITIONAL_GOLD_DROP("tooltip.gemstones.event_name.additional_gold_drop"),
  POTION_DURATION("tooltip.gemstones.event_name.potion_duration"),
  REGENERATE_BLOCK("tooltip.gemstones.event_name.regenerate_block"),
  COPY_ENTITY_DROP("tooltip.gemstones.event_name.copy_entity_drop"),
  ADDITIONAL_DAMAGE("tooltip.gemstones.event_name.additional_damage"),
  SMELTER("tooltip.gemstones.event_name.smelter"),
  SMALL_FLAT_EXPLOSION("tooltip.gemstones.event_name.small_flat_explode");

  private final String translationString;

  EventType(String translationString) {
    this.translationString = translationString;
  }

  public String getTranslatioString() {
    return translationString;
  }
};
