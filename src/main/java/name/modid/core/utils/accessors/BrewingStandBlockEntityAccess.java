package name.modid.core.utils.accessors;

import java.util.UUID;

public interface BrewingStandBlockEntityAccess {
  void setLastBrewer(UUID uuid);

  UUID getLastBrewer();
}