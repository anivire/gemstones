package name.modid.core.api.modifiers.types;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import name.modid.Gemstones;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public final class EventType {
  private final Identifier id;
  private final String name;
  private final String translationKey;

  public static final RegistryKey<Registry<EventType>> REGISTRY_KEY = RegistryKey
      .ofRegistry(Identifier.of(Gemstones.MOD_ID, "event_type"));
  public static final Registry<EventType> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY)
      .buildAndRegister();

  private static final Map<String, EventType> BY_NAME = new ConcurrentHashMap<>();

  public static final EventType ON_HIT_LIFE_STEAL = register("ON_HIT_LIFE_STEAL",
      "tooltip.gemstones.event_name.lifesteal");
  public static final EventType ON_HIT_LIGHTNING_BOLT = register("ON_HIT_LIGHTNING_BOLT",
      "tooltip.gemstones.event_name.lighting_bolt");
  public static final EventType ON_HIT_TORRENT = register("ON_HIT_TORRENT", "tooltip.gemstones.event_name.torrent");
  public static final EventType ON_HIT_SMALL_FLAT_EXPLOSION = register("ON_HIT_SMALL_FLAT_EXPLOSION",
      "tooltip.gemstones.event_name.small_flat_explode");
  public static final EventType ON_HIT_COPY_ENTITY_DROP = register("ON_HIT_COPY_ENTITY_DROP",
      "tooltip.gemstones.event_name.copy_entity_drop");
  public static final EventType ON_HIT_MULTIPLY_DAMAGE_ARMORLESS = register("ON_HIT_MULTIPLY_DAMAGE_ARMORLESS",
      "tooltip.gemstones.event_name.multiply_damage_armorless");
  public static final EventType ON_HIT_MAGIC_STRIKE = register("ON_HIT_MAGIC_STRIKE",
      "tooltip.gemstones.event_name.magic_strike");
  public static final EventType ON_HIT_ENDER_JUDGEMENT = register("ON_HIT_ENDER_JUDGEMENT",
      "tooltip.gemstones.event_name.ender_judgement");
  public static final EventType ON_HIT_ENTITY_PULL = register("ON_HIT_ENTITY_PULL",
      "tooltip.gemstones.event_name.entity_pull");
  public static final EventType ON_HIT_ARROW_RAIN = register("ON_HIT_ARROW_RAIN",
      "tooltip.gemstones.event_name.arrow_rain");
  public static final EventType ON_HIT_RANDOM_EFFECT = register("ON_HIT_RANDOM_EFFECT",
      "tooltip.gemstones.event_name.target_random_effect");
  public static final EventType ON_HIT_EXP_ADDITIONAL_DAMAGE = register("ON_HIT_EXP_ADDITIONAL_DAMAGE",
      "tooltip.gemstones.event_name.exp_additional_damage");
  public static final EventType ON_FIRST_HIT_ADDITIONAL_DAMAGE = register("ON_FIRST_HIT_ADDITIONAL_DAMAGE",
      "tooltip.gemstones.event_name.additional_damage");

  public static final EventType ON_BLOCK_BREAK_ADDITIONAL_GOLD_DROP = register("ON_BLOCK_BREAK_ADDITIONAL_GOLD_DROP",
      "tooltip.gemstones.event_name.additional_gold_drop");
  public static final EventType ON_BLOCK_BREAK_REGENERATE_BLOCK = register("ON_BLOCK_BREAK_REGENERATE_BLOCK",
      "tooltip.gemstones.event_name.regenerate_block");
  public static final EventType ON_BLOCK_BREAK_SMELTER = register("ON_BLOCK_BREAK_SMELTER",
      "tooltip.gemstones.event_name.smelter");
  public static final EventType ON_BLOCK_BREAK_ENCHANTER = register("ON_BLOCK_BREAK_ENCHANTER",
      "tooltip.gemstones.event_name.block_enchanter");
  public static final EventType ON_BLOCK_BREAK_EXTRA_HEALTH = register("ON_BLOCK_BREAK_EXTRA_HEALTH",
      "tooltip.gemstones.event_name.extra_health");
  public static final EventType ON_BLOCK_BREAK_HEAL = register("ON_BLOCK_BREAK_HEAL",
      "tooltip.gemstones.event_name.heal");
  public static final EventType ON_BLOCK_BREAK_MINER = register("ON_BLOCK_BREAK_MINER",
      "tooltip.gemstones.event_name.miner");
  public static final EventType ON_BLOCK_BREAK_RANDOM_ITEM_DROP = register("ON_BLOCK_BREAK_RANDOM_ITEM_DROP",
      "tooltip.gemstones.event_name.random_item_drop");

  public static final EventType ON_POTION_BREW_INCREASE_DURATION = register("ON_POTION_BREW_INCREASE_DURATION",
      "tooltip.gemstones.event_name.potion_duration");
  public static final EventType ON_FISHING_INCREASE_MOSSY_BOX_DROP = register("ON_FISHING_INCREASE_MOSSY_BOX_DROP",
      "tooltip.gemstones.event_name.increase_mossy_box_drop");

  public static final EventType PLAYER_WITHER_GUARD = register("PLAYER_WITHER_GUARD",
      "tooltip.gemstones.event_name.wither_guard");
  public static final EventType PLAYER_PROJECTILE_IMMUNE = register("PLAYER_PROJECTILE_IMMUNE",
      "tooltip.gemstones.event_name.projectile_immune");
  public static final EventType PLAYER_RANDOM_EFFECT = register("PLAYER_RANDOM_EFFECT",
      "tooltip.gemstones.event_name.player_random_effect");
  public static final EventType PLAYER_BONUS_DAMAGE_MISSING_HEALTH = register("PLAYER_BONUS_DAMAGE_MISSING_HEALTH",
      "tooltip.gemstones.event_name.bonus_missing_health");
  public static final EventType PLAYER_SAVE_LETHAL = register("PLAYER_SAVE_LETHAL",
      "tooltip.gemstones.event_name.save_lethal");

  public static final EventType PLAYER_TICK_ENDER_CYCLE = register("PLAYER_TICK_ENDER_CYCLE",
      "tooltip.gemstones.event_name.ender_cycle");
  public static final EventType PLAYER_TICK_ORE_VISION = register("PLAYER_TICK_ORE_VISION",
      "tooltip.gemstones.event_name.ore_vision");

  public static final EventType AFTER_DEATH_DETONATE = register("AFTER_DEATH_DETONATE",
      "tooltip.gemstones.event_name.detonate");
  public static final EventType AFTER_DEATH_HARVEST_MARK = register("AFTER_DEATH_HARVEST_MARK",
      "tooltip.gemstones.event_name.harvest_mark");
  public static final EventType AFTER_DEATH_ADDITIONAL_EXP_GAIN = register("AFTER_DEATH_ADDITIONAL_EXP_GAIN",
      "tooltip.gemstones.event_name.additional_exp_gain");

  public static final EventType WORLD_EVENT_INCREASE_MOB_SPAWNRATE = register("WORLD_EVENT_INCREASE_MOB_SPAWNRATE",
      "tooltip.gemstones.event_name.increase_mob_spawnrate");

  private EventType(String name, String translationKey) {
    this.name = name;
    this.translationKey = translationKey;
    this.id = Identifier.of(Gemstones.MOD_ID, name.toLowerCase(Locale.ROOT));
  }

  private static EventType register(String name, String translationKey) {
    EventType type = new EventType(name, translationKey);
    Registry.register(REGISTRY, type.id, type);
    BY_NAME.put(name, type);
    return type;
  }

  public static EventType fromString(String raw) {
    if (raw == null) {
      return null;
    }

    String upper = raw.toUpperCase(Locale.ROOT);
    EventType byLegacyName = BY_NAME.get(upper);
    if (byLegacyName != null) {
      return byLegacyName;
    }

    Identifier id = raw.contains(":")
        ? Identifier.of(raw)
        : Identifier.of(Gemstones.MOD_ID, raw.toLowerCase(Locale.ROOT));

    return REGISTRY.getOrEmpty(id)
        .orElseThrow(() -> new IllegalArgumentException("Unknown event type: " + raw));
  }

  public Identifier getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getTranslationKey() {
    return translationKey;
  }

  @Override
  public String toString() {
    return name;
  }

  public static void initialize() {
  }
}
