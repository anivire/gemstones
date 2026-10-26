package name.modid.compat.cloth;

import dev.architectury.platform.Platform;

public final class ClothConfigCompat {
  private ClothConfigCompat() {
  }

  public static boolean isLoaded() {
    return Platform.isModLoaded("cloth-config") || Platform.isModLoaded("cloth_config");
  }
}
