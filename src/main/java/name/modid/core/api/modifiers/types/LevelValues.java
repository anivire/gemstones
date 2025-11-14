package name.modid.core.api.modifiers.types;

import java.util.ArrayList;
import java.util.stream.Stream;

public class LevelValues {
  private final ArrayList<Double> values;

  public LevelValues(ArrayList<Double> values) {
    this.values = new ArrayList<>(values);
  }

  public Double get(GemstoneQuality quality) {
    if (values.isEmpty()) {
      return 0.0;
    }

    if (quality == GemstoneQuality.UNUSUAL) {
      return values.get(0);
    }

    int index = quality.getValue();
    if (index < 0) {
      return 0.0;
    }

    if (index >= values.size()) {
      return values.get(0);
    }

    return values.get(index);
  }

  public Stream<Double> stream() {
    return values.stream();
  }
}