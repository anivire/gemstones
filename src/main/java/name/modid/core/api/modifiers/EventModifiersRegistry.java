package name.modid.core.api.modifiers;

import java.util.List;
import java.util.Map;

import name.modid.core.api.modifiers.types.EventCategory;
import name.modid.core.api.modifiers.types.EventType;

public class EventModifiersRegistry {

  public static final Map<EventCategory, List<EventType>> REGISTRY = Map.of(
      EventCategory.ON_HIT, List.of(
          EventType.LIFE_STEAL,
          EventType.LIGHTNING_BOLT,
          EventType.TORRENT,
          EventType.SMALL_FLAT_EXPLOSION,
          EventType.ADDITIONAL_DAMAGE),
      EventCategory.ON_BLOCK_BREAK, List.of(
          EventType.INCREASE_GEODES_DROP,
          EventType.ADDITIONAL_GOLD_DROP,
          EventType.REGENERATE_BLOCK,
          EventType.SMELTER),
      EventCategory.ON_DROP, List.of(
          EventType.INCREASE_MOSSY_BOX_DROP,
          EventType.COPY_ENTITY_DROP),
      EventCategory.PLAYER_EVENT, List.of(
          EventType.EXTRA_HEALTH,
          EventType.HEAL,
          EventType.POTION_DURATION),
      EventCategory.WORLD_EVENT, List.of(
          EventType.INCREASE_MOB_SPAWNRATE));

  public static List<EventType> get(EventCategory category) {
    return REGISTRY.getOrDefault(category, List.of());
  }
}