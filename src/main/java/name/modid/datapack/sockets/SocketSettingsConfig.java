package name.modid.datapack.sockets;

import com.google.gson.annotations.SerializedName;

public class SocketSettingsConfig {
  public static final int DEFAULT_MAX_SLOTS = 5;

  @SerializedName("max_slots")
  public int maxSlots = DEFAULT_MAX_SLOTS;

  public void merge(SocketSettingsConfig other) {
    if (other == null) {
      return;
    }

    maxSlots = Math.max(0, other.maxSlots);
  }
}
