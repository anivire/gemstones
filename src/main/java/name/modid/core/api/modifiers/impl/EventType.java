package name.modid.core.api.modifiers.impl;

public enum EventType {
  // ON_HIT
  LIFE_STEAL(EventCategory.ON_HIT, "tooltip.gemstones.event_name.lifesteal"),
  LIGHTNING_BOLT(EventCategory.ON_HIT, "tooltip.gemstones.event_name.lighting_bolt"),
  TORRENT(EventCategory.ON_HIT, "tooltip.gemstones.event_name.torrent"),
  SMALL_FLAT_EXPLOSION(EventCategory.ON_HIT, "tooltip.gemstones.event_name.small_flat_explode"),
  ADDITIONAL_DAMAGE(EventCategory.ON_HIT, "tooltip.gemstones.event_name.additional_damage"),

  // ON_BLOCK_BREAK
  INCREASE_GEODES_DROP(EventCategory.ON_BLOCK_BREAK, "tooltip.gemstones.event_name.increase_geodes_ON_DROP"),
  ADDITIONAL_GOLD_DROP(EventCategory.ON_BLOCK_BREAK, "tooltip.gemstones.event_name.additional_gold_ON_DROP"),
  REGENERATE_BLOCK(EventCategory.ON_BLOCK_BREAK, "tooltip.gemstones.event_name.regenerate_block"),
  SMELTER(EventCategory.ON_BLOCK_BREAK, "tooltip.gemstones.event_name.smelter"),

  // ON_DROP
  INCREASE_MOSSY_BOX_DROP(EventCategory.ON_BLOCK_BREAK, "tooltip.gemstones.event_name.increase_mossy_box_ON_DROP"),
  COPY_ENTITY_DROP(EventCategory.ON_DROP, "tooltip.gemstones.event_name.copy_entity_ON_DROP"),

  // PLAYER_EVENT
  EXTRA_HEALTH(EventCategory.PLAYER_EVENT, "tooltip.gemstones.event_name.extra_health"),
  HEAL(EventCategory.PLAYER_EVENT, "tooltip.gemstones.event_name.heal"),
  POTION_DURATION(EventCategory.PLAYER_EVENT, "tooltip.gemstones.event_name.potion_duration"),

  // WORLD_EVENT
  INCREASE_MOB_SPAWNRATE(EventCategory.WORLD_EVENT, "tooltip.gemstones.event_name.increase_mob_spawnrate");

  private final EventCategory category;
  private final String getTranslationKey;

  EventType(EventCategory category, String getTranslationKey) {
    this.category = category;
    this.getTranslationKey = getTranslationKey;
  }

  public EventCategory getCategory() {
    return category;
  }

  public String getTranslationKey() {
    return getTranslationKey;
  }
}