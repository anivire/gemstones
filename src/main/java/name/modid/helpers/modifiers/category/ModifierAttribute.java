package name.modid.helpers.modifiers.category;

import java.util.ArrayList;
import java.util.List;

import name.modid.helpers.modifiers.instance.GemstoneModifier;
import name.modid.helpers.modifiers.tooltips.GemstoneTooltipBuilder;
import name.modid.helpers.modifiers.type.ModifierItemCategory;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;

public class ModifierAttribute implements GemstoneModifier {
  public Operation operation;
  public ModifierItemCategory itemType;
  public ArrayList<Double> modifierValuesList = new ArrayList<Double>();
  public RegistryEntry<EntityAttribute> attr;
  public GemstoneType gemstoneType;
  public GemstoneRarity rarityType;

  public ModifierAttribute(Operation operation, List<Double> valueLevels,
      ModifierItemCategory itemType, RegistryEntry<EntityAttribute> attr,
      GemstoneType gemstoneType) {
    this.operation = operation;
    this.modifierValuesList = new ArrayList<Double>(valueLevels);
    this.itemType = itemType;
    this.attr = attr;
    this.gemstoneType = gemstoneType;
  }

  public MutableText getTooltipString(GemstoneRarity gemstoneRarityType,
      Boolean withCategoryString) {
    this.rarityType = gemstoneRarityType;
    return new GemstoneTooltipBuilder(this.gemstoneType, this.itemType, (ModifierAttribute) this)
        .withCategoryString(withCategoryString)
        .build(this.rarityType);
  }

  public GemstoneType getGemstoneType() {
    return this.gemstoneType;
  }

  public GemstoneRarity getRarityType() {
    return this.rarityType;
  }

  public void setRarityType(GemstoneRarity rarityType) {
    this.rarityType = rarityType;
  }

  public ModifierItemCategory getItemCategory() {
    return this.itemType;
  };
}
