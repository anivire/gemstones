package name.modid.core.utils.oreVision;

public final class OreVisionRadius {
  private OreVisionRadius() {
  }

  public static int fromValue(double value) {
    return Math.max(1, Math.round((float) value));
  }
}
