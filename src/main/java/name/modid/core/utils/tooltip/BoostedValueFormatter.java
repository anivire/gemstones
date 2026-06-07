package name.modid.core.utils.tooltip;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.DoubleUnaryOperator;

public final class BoostedValueFormatter {
  private BoostedValueFormatter() {
  }

  public static String format(double value, String postfix) {
    BigDecimal v = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
    return postfix.isBlank() ? v.toPlainString() : v.toPlainString() + postfix;
  }

  public static boolean isBoosted(double baseValue, double boostedValue) {
    return Double.compare(baseValue, boostedValue) != 0;
  }

  public static String formatBaseWithBoost(double baseValue, double boostedValue, String postfix) {
    String baseFormatted = format(baseValue, postfix);
    if (!isBoosted(baseValue, boostedValue)) {
      return baseFormatted;
    }

    return baseFormatted + " (" + format(boostedValue, postfix) + ")";
  }

  public static String formatAdjustedBaseWithBoost(
      double baseValue,
      double boostedValue,
      DoubleUnaryOperator adjustment,
      String postfix) {
    return formatBaseWithBoost(
        Math.abs(adjustment.applyAsDouble(baseValue)),
        Math.abs(adjustment.applyAsDouble(boostedValue)),
        postfix);
  }
}
