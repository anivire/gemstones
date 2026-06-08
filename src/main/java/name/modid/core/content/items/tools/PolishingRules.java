package name.modid.core.content.items.tools;

import net.minecraft.util.math.random.Random;

public final class PolishingRules {
  private static final int MIN_STAGE_DURATION_TICKS = 20;
  private static final int MAX_STAGE_DURATION_TICKS = 30;

  private PolishingRules() {
  }

  public static int minStageDurationTicks() {
    return MIN_STAGE_DURATION_TICKS;
  }

  public static int maxStageDurationTicks() {
    return MAX_STAGE_DURATION_TICKS;
  }

  public static int nextStageDurationTicks(Random random) {
    return MIN_STAGE_DURATION_TICKS + random.nextInt(MAX_STAGE_DURATION_TICKS - MIN_STAGE_DURATION_TICKS + 1);
  }
}
