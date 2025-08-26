package name.modid.helpers.modifiers.category;

import java.util.ArrayList;

import name.modid.helpers.GemstoneRarity;
import name.modid.helpers.GemstoneType;
import name.modid.helpers.modifiers.instance.AbstractModifier;
import name.modid.helpers.modifiers.instance.LevelValues;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.registry.entry.RegistryEntry;

public class ModifierAttribute extends AbstractModifier {
  private LevelValues values;
  private Operation operation;
  private RegistryEntry<EntityAttribute> attributeEntry;

  public ModifierAttribute(
      GemstoneType gemstoneType,
      GemstoneRarity rarityType,
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
