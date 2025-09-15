package name.modid.core.api.modifiers.types;

import java.util.ArrayList;

public class LevelValues {
  private final ArrayList<Double> values;

  public LevelValues(ArrayList<Double> values) {
    this.values = new ArrayList<Double>(values);
  }

  public Double get(GemstoneQuality rarity) {
    return values.get(rarity.getValue());
  }
}
