package name.modid.datapack.drops;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import name.modid.core.api.modifiers.types.GemstoneQuality;
import net.minecraft.util.Identifier;

public class DropsRegistry {
  private static final Map<String, Object> CACHED = new ConcurrentHashMap<>();

  public static DropsConfig getConfig() {
    return DropsDataLoader.getConfig();
  }

  public static Map<GemstoneQuality, Float> getQualityDistribution() {
    return getConfig().getQuality().toMap();
  }

  public static List<Identifier> getLootTouchTables() {
    return getConfig().getLootTouchTables().stream()
        .map(Identifier::of)
        .toList();
  }

  public static List<DropsConfig.LootTableDrop> getStructuresLoot() {
    return getConfig().getStructuresLoot();
  }

  public static List<DropsConfig.LootTableDrop> getEntitiesLoot() {
    return getConfig().getEntitiesLoot();
  }

  public static Map<String, DropsConfig.BlockDropEntry> getBlockDrops() {
    return getConfig().getBlockDrops();
  }

  public static DropsConfig.SpecialDrop getSpecialDrop(String trigger) {
    return getConfig().getSpecialDrops().stream()
        .filter(d -> d.getTrigger().equals(trigger))
        .findFirst()
        .orElse(null);
  }

  public static void clearCache() {
    CACHED.clear();
  }
}
