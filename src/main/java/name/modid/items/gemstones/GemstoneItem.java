package name.modid.items.gemstones;

import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.item.Item;

public class GemstoneItem extends Item {
  protected GemstoneType gemstoneType;
  protected GemstoneRarity rarityType;

  public GemstoneItem(Settings settings, GemstoneType gemstoneType, GemstoneRarity rarityType) {
    super(settings);

    this.gemstoneType = gemstoneType;
    this.rarityType = rarityType;
  }

  public GemstoneType getType() {
    return this.gemstoneType;
  }

  public GemstoneRarity getRarityType() {
    return this.rarityType;
  }
}
