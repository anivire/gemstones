package name.modid.datapack.items;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import name.modid.core.api.modifiers.types.ModifierItemCategory;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ItemCompatibilityRegistry {
  private static final Map<Identifier, ModifierItemCategory> ITEM_CATEGORIES = new ConcurrentHashMap<>();
  private static final Map<TagKey<Item>, ModifierItemCategory> TAG_CATEGORIES = new ConcurrentHashMap<>();
  private static final Set<Identifier> BLACKLISTED_ITEMS = ConcurrentHashMap.newKeySet();
  private static final Set<TagKey<Item>> BLACKLISTED_TAGS = ConcurrentHashMap.newKeySet();

  private ItemCompatibilityRegistry() {
  }

  public static void rebuild(Map<String, ItemCompatibilityConfig> configs) {
    ITEM_CATEGORIES.clear();
    TAG_CATEGORIES.clear();
    BLACKLISTED_ITEMS.clear();
    BLACKLISTED_TAGS.clear();

    configs.values().forEach(config -> {
      if (config.blacklist) {
        config.items.forEach(BLACKLISTED_ITEMS::add);
        config.tags.stream().map(ItemCompatibilityRegistry::itemTag).forEach(BLACKLISTED_TAGS::add);
        return;
      }

      config.items.forEach(item -> ITEM_CATEGORIES.put(item, config.category));
      config.tags.stream()
          .map(ItemCompatibilityRegistry::itemTag)
          .forEach(tag -> TAG_CATEGORIES.put(tag, config.category));
    });
  }

  public static boolean isBlacklisted(Item item) {
    Identifier id = Registries.ITEM.getId(item);
    if (BLACKLISTED_ITEMS.contains(id)) {
      return true;
    }

    return BLACKLISTED_TAGS.stream().anyMatch(tag -> item.getDefaultStack().isIn(tag));
  }

  public static Optional<ModifierItemCategory> getConfiguredCategory(Item item) {
    Identifier id = Registries.ITEM.getId(item);
    ModifierItemCategory itemCategory = ITEM_CATEGORIES.get(id);
    if (itemCategory != null) {
      return Optional.of(itemCategory);
    }

    return TAG_CATEGORIES.entrySet().stream()
        .filter(entry -> item.getDefaultStack().isIn(entry.getKey()))
        .map(Map.Entry::getValue)
        .findFirst();
  }

  private static TagKey<Item> itemTag(Identifier id) {
    return TagKey.of(RegistryKeys.ITEM, id);
  }
}
