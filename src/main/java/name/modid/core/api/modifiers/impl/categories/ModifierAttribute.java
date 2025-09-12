package name.modid.core.api.modifiers.impl.categories;

import java.util.ArrayList;

import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.LevelValues;
import name.modid.core.api.modifiers.ModifierItemCategory;
import name.modid.core.api.modifiers.impl.AbstractModifier;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.registry.entry.RegistryEntry;

public class ModifierAttribute extends AbstractModifier {
  private LevelValues values;
  private Operation operation;
  private RegistryEntry<EntityAttribute> attributeEntry;

  public ModifierAttribute(
      GemstoneType gemstoneType,
      GemstoneQuality rarityType,
      ModifierItemCategory itemCategory,
      ArrayList<Double> values,
      Operation operation,
      RegistryEntry<EntityAttribute> attributeEntry) {
    super(gemstoneType, itemCategory, rarityType);

    this.operation = operation;
    this.attributeEntry = attributeEntry;
    this.values = new LevelValues(values);
  }

  public LevelValues getLevelValues() {
    return values;
  }

  public Operation getOperation() {
    return operation;
  }

  public RegistryEntry<EntityAttribute> getAttributeEntry() {
    return attributeEntry;
  }
}
