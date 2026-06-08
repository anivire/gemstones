package name.modid.core.utils.witherGuard;

public final class WitherGuardSkullLimit {
  private WitherGuardSkullLimit() {
  }

  public static int fromValue(double value) {
    return Math.max(1, Math.round((float) value));
  }
}
