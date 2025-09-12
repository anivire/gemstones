package name.modid.datapack.geodes;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;

public class GeodesConfig {
  @SerializedName("geode_id")
  public String geodeId;
  public Map<GemstoneType, Float> gemstones;
  public Map<GemstoneQuality, Float> qualities;
}