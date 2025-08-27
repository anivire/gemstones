package name.modid.helpers.modifiers.instance;

import java.util.ArrayList;

import name.modid.helpers.GemstoneRarity;

public class LevelValues {
  private final ArrayList<Double> values;

  public LevelValues(ArrayList<Double> values) {
    this.values = new ArrayList<Double>(values);
  }

  public Double get(GemstoneRarity rarity) {
    return values.get(rarity.getValue());
  }
}
