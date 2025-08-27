package name.modid.helpers.modifiers.category;

import java.util.ArrayList;
import java.util.List;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.instance.AbstractModifier;
import name.modid.helpers.modifiers.type.ModifierItemCategory;

public class ModifierMultiplyAttribute extends AbstractModifier {
  private final List<ModifierAttribute> instances;

  public ModifierMultiplyAttribute(
      GemstoneType gemstoneType,
      GemstoneRarity rarityType,
      ModifierItemCategory itemCategory,
      List<ModifierAttribute> instances) {
    super(gemstoneType, itemCategory, rarityType);

    this.instances = new ArrayList<>(instances);
  }

  public List<ModifierAttribute> getInstances() {
    return instances;
  }
}