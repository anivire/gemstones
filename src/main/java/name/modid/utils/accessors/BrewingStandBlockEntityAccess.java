package name.modid.utils.accessors;

import java.util.UUID;

public interface BrewingStandBlockEntityAccess {
  void setLastBrewer(UUID uuid);

  UUID getLastBrewer();
}