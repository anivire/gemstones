package name.modid.core.api.modifiers.types;

public enum EventType {
  ON_HIT_LIFE_STEAL("tooltip.gemstones.event_name.lifesteal"),
  ON_HIT_LIGHTNING_BOLT("tooltip.gemstones.event_name.lighting_bolt"),
  ON_HIT_TORRENT("tooltip.gemstones.event_name.torrent"),
  ON_HIT_SMALL_FLAT_EXPLOSION("tooltip.gemstones.event_name.small_flat_explode"),
  ON_HIT_COPY_ENTITY_DROP("tooltip.gemstones.event_name.copy_entity_drop"),
  ON_HIT_MULTIPLY_DAMAGE_ARMORLESS("tooltip.gemstones.event_name.multiply_damage_armorless"),

  ON_FIRST_HIT_ADDITIONAL_DAMAGE("tooltip.gemstones.event_name.additional_damage"),

  ON_BLOCK_BREAK_INCREASE_GEODES_DROP("tooltip.gemstones.event_name.increase_geodes_drop"),
  ON_BLOCK_BREAK_ADDITIONAL_GOLD_DROP("tooltip.gemstones.event_name.additional_gold_drop"),
  ON_BLOCK_BREAK_REGENERATE_BLOCK("tooltip.gemstones.event_name.regenerate_block"),
  ON_BLOCK_BREAK_SMELTER("tooltip.gemstones.event_name.smelter"),
  ON_BLOCK_BREAK_EXTRA_HEALTH("tooltip.gemstones.event_name.extra_health"),
  ON_BLOCK_BREAK_HEAL("tooltip.gemstones.event_name.heal"),
  ON_BLOCK_BREAK_MINER("tooltip.gemstones.event_name.miner"),

  WORLD_EVENT_INCREASE_MOB_SPAWNRATE("tooltip.gemstones.event_name.increase_mob_spawnrate"),

  ON_POTION_BREW_INCREASE_DURATION("tooltip.gemstones.event_name.potion_duration"),

  ON_FISHING_INCREASE_MOSSY_BOX_DROP("tooltip.gemstones.event_name.increase_mossy_box_drop"),

  PLAYER_WITHER_GUARD("tooltip.gemstones.event_name.wither_guard"),
  PLAYER_PROJECTILE_IMMUNE("tooltip.gemstones.event_name.projectile_immune"),

  AFTER_DEATH_DETONATE("tooltip.gemstones.event_name.detonate"),
  AFTER_DEATH_HARVEST_MARK("tooltip.gemstones.event_name.harvest_mark"),

  ON_HIT_RANDOM_EFFECT("tooltip.gemstones.event_name.target_random_effect"),
  ON_BLOCK_BREAK_RANDOM_ITEM_DROP("tooltip.gemstones.event_name.random_item_drop"),

  PLAYER_RANDOM_EFFECT("tooltip.gemstones.event_name.player_random_effect"),
  PLAYER_SAVE_LETHAL("tooltip.gemstones.event_name.save_lethal");

  private final String getTranslationKey;

  EventType(String getTranslationKey) {
    this.getTranslationKey = getTranslationKey;
  }

  public String getTranslationKey() {
    return getTranslationKey;
  }
}