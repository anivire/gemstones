package name.modid.utils;

import java.util.UUID;

public interface BrewingStandBlockEntityAccess {
  void setLastBrewer(UUID uuid);

  UUID getLastBrewer();
}