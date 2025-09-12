package name.modid.core.api.modifiers.impl.categories;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.ModifierItemCategory;
import name.modid.core.api.modifiers.impl.AbstractModifier;

public class ModifierMultiplyAttribute extends AbstractModifier {
  private final List<ModifierAttribute> instances;

  public ModifierMultiplyAttribute(
      GemstoneType gemstoneType,
      GemstoneQuality rarityType,
      ModifierItemCategory itemCategory,
      List<ModifierAttribute> instances) {
    super(gemstoneType, itemCategory, rarityType);

    this.instances = new ArrayList<>(instances);
  }

  public List<ModifierAttribute> getInstances() {
    return instances;
  }
}