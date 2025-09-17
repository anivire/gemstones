package name.modid.core.api.modifiers.types;

public enum EventType {
  ON_HIT_LIFE_STEAL("tooltip.gemstones.event_name.lifesteal"),
  ON_HIT_LIGHTNING_BOLT("tooltip.gemstones.event_name.lighting_bolt"),
  ON_HIT_TORRENT("tooltip.gemstones.event_name.torrent"),
  ON_HIT_SMALL_FLAT_EXPLOSION("tooltip.gemstones.event_name.small_flat_explode"),
  ON_HIT_ADDITIONAL_DAMAGE("tooltip.gemstones.event_name.additional_damage"),

  ON_BLOCK_BREAK_INCREASE_GEODES_DROP("tooltip.gemstones.event_name.increase_geodes_drop"),
  ON_BLOCK_BREAK_ADDITIONAL_GOLD_DROP("tooltip.gemstones.event_name.additional_gold_drop"),
  ON_BLOCK_BREAK_REGENERATE_BLOCK("tooltip.gemstones.event_name.regenerate_block"),
  ON_BLOCK_BREAK_SMELTER("tooltip.gemstones.event_name.smelter"),

  ON_DROP_INCREASE_MOSSY_BOX_DROP("tooltip.gemstones.event_name.increase_mossy_box_drop"),
  ON_DROP_COPY_ENTITY_DROP("tooltip.gemstones.event_name.copy_entity_drop"),

  PLAYER_EVENT_EXTRA_HEALTH("tooltip.gemstones.event_name.extra_health"),
  PLAYER_EVENT_HEAL("tooltip.gemstones.event_name.heal"),
  PLAYER_EVENT_POTION_DURATION("tooltip.gemstones.event_name.potion_duration"),

  WORLD_EVENT_INCREASE_MOB_SPAWNRATE("tooltip.gemstones.event_name.increase_mob_spawnrate");

  private final String getTranslationKey;

  EventType(String getTranslationKey) {
    this.getTranslationKey = getTranslationKey;
  }

  public String getTranslationKey() {
    return getTranslationKey;
  }
}