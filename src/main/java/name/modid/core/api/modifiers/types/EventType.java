package name.modid.core.api.modifiers.types;

public enum EventType {
  // ON_HIT
  LIFE_STEAL("tooltip.gemstones.event_name.lifesteal"),
  LIGHTNING_BOLT("tooltip.gemstones.event_name.lighting_bolt"),
  TORRENT("tooltip.gemstones.event_name.torrent"),
  SMALL_FLAT_EXPLOSION("tooltip.gemstones.event_name.small_flat_explode"),
  ADDITIONAL_DAMAGE("tooltip.gemstones.event_name.additional_damage"),

  // ON_BLOCK_BREAK
  INCREASE_GEODES_DROP("tooltip.gemstones.event_name.increase_geodes_ON_DROP"),
  ADDITIONAL_GOLD_DROP("tooltip.gemstones.event_name.additional_gold_ON_DROP"),
  REGENERATE_BLOCK("tooltip.gemstones.event_name.regenerate_block"),
  SMELTER("tooltip.gemstones.event_name.smelter"),

  // ON_DROP
  INCREASE_MOSSY_BOX_DROP("tooltip.gemstones.event_name.increase_mossy_box_ON_DROP"),
  COPY_ENTITY_DROP("tooltip.gemstones.event_name.copy_entity_ON_DROP"),

  // PLAYER_EVENT
  EXTRA_HEALTH("tooltip.gemstones.event_name.extra_health"),
  HEAL("tooltip.gemstones.event_name.heal"),
  POTION_DURATION("tooltip.gemstones.event_name.potion_duration"),

  // WORLD_EVENT
  INCREASE_MOB_SPAWNRATE("tooltip.gemstones.event_name.increase_mob_spawnrate");

  private final String getTranslationKey;

  EventType(String getTranslationKey) {
    this.getTranslationKey = getTranslationKey;
  }

  public String getTranslationKey() {
    return getTranslationKey;
  }
}