package name.modid.datapack.drops;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import net.minecraft.util.Identifier;

public class DropsConfig {
  @SerializedName("gemstones_drop_quality")
  private QualityConfig quality;
  @SerializedName("touch_drops")
  private List<String> lootTouchTables;
  @SerializedName("structures_loot")
  private List<LootTableDrop> structuresLoot;
  @SerializedName("entities_loot")
  private List<LootTableDrop> entitiesLoot;
  @SerializedName("block_drops")
  private Map<String, BlockDropEntry> blockDrops;
  @SerializedName("special_drops")
  private List<SpecialDrop> specialDrops;

  public QualityConfig getQuality() {
    return quality != null ? quality : new QualityConfig();
  }

  public List<String> getLootTouchTables() {
    return lootTouchTables != null ? lootTouchTables : List.of();
  }

  public List<LootTableDrop> getStructuresLoot() {
    return structuresLoot != null ? structuresLoot : List.of();
  }

  public List<LootTableDrop> getEntitiesLoot() {
    return entitiesLoot != null ? entitiesLoot : List.of();
  }

  public Map<String, BlockDropEntry> getBlockDrops() {
    return blockDrops != null ? blockDrops : Map.of();
  }

  public List<SpecialDrop> getSpecialDrops() {
    return specialDrops != null ? specialDrops : List.of();
  }

  void merge(DropsConfig other) {
    if (other.quality != null)
      quality = other.quality;
    if (other.lootTouchTables != null)
      lootTouchTables = other.lootTouchTables;
    if (other.structuresLoot != null)
      structuresLoot = other.structuresLoot;
    if (other.entitiesLoot != null)
      entitiesLoot = other.entitiesLoot;
    if (other.blockDrops != null)
      blockDrops = other.blockDrops;
    if (other.blockDrops != null)
      blockDrops = other.blockDrops;
    if (other.specialDrops != null)
      specialDrops = other.specialDrops;
  }

  void seal() {
    if (quality == null)
      quality = new QualityConfig();
    if (lootTouchTables == null)
      lootTouchTables = List.of();
    if (structuresLoot == null)
      structuresLoot = List.of();
    if (entitiesLoot == null)
      entitiesLoot = List.of();
    if (blockDrops == null)
      blockDrops = Map.of();
    if (specialDrops == null)
      specialDrops = List.of();
  }

  public static class QualityConfig {
    // Placeholder values
    @SerializedName("CRUDE")
    public float crude = 0.60f;
    @SerializedName("POLISHED")
    public float polished = 0.25f;
    @SerializedName("FLAWLESS")
    public float flawless = 0.10f;
    @SerializedName("RADIANT")
    public float radiant = 0.05f;

    public Map<GemstoneQuality, Float> toMap() {
      return Map.of(
          GemstoneQuality.CRUDE, crude,
          GemstoneQuality.POLISHED, polished,
          GemstoneQuality.FLAWLESS, flawless,
          GemstoneQuality.RADIANT, radiant);
    }
  }

  public static class LootTableDrop {
    @SerializedName("loot_tables")
    public List<String> lootTables;
    public List<PoolEntry> pools;

    public List<String> getLootTables() {
      return lootTables != null ? lootTables : List.of();
    }

    public List<PoolEntry> getPools() {
      return pools != null ? pools : List.of();
    }
  }

  public static class PoolEntry {
    @SerializedName("gemstone_type")
    public GemstoneType gemstoneType;
    public float chance;
    public boolean mythic;

    public GemstoneType getGemstoneType() {
      return gemstoneType;
    }

    public float getChance() {
      return chance;
    }

    public boolean isMythic() {
      return mythic;
    }
  }

  public static class DropEntry {
    public Identifier item;
    @SerializedName("gemstone_type")
    public GemstoneType gemstoneType;
    public float chance;

    public Identifier getItem() {
      return item;
    }

    public GemstoneType getGemstoneType() {
      return gemstoneType;
    }

    public float getChance() {
      return chance;
    }
  }

  public static class BlockDropEntry {
    public List<DropEntry> entries;

    public List<DropEntry> getEntries() {
      return entries != null ? entries : List.of();
    }
  }

  public static class SpecialDrop {
    public String trigger;
    public Identifier item;
    @SerializedName("gemstone_type")
    public GemstoneType gemstoneType;
    public float chance;
    @SerializedName("min_count")
    public int minCount = 1;
    @SerializedName("max_count")
    public int maxCount = 1;
    public boolean mythic;

    public String getTrigger() {
      return trigger;
    }

    public Identifier getItem() {
      return item;
    }

    public GemstoneType getGemstoneType() {
      return gemstoneType;
    }

    public float getChance() {
      return chance;
    }

    public int getMinCount() {
      return minCount;
    }

    public int getMaxCount() {
      return maxCount;
    }

    public boolean isMythic() {
      return mythic;
    }
  }
}
