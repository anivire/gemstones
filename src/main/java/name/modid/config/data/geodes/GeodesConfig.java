package name.modid.config.data.geodes;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;

public class GeodesConfig {
  @SerializedName("geode_id")
  public String geodeId;
  public Map<GemstoneType, Float> gemstones;
  public Map<GemstoneRarity, Float> rarities;
}