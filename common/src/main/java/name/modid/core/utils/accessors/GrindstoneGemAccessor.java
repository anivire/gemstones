package name.modid.core.utils.accessors;

import net.minecraft.item.ItemStack;

public interface GrindstoneGemAccessor {
  /** The gemstone being ground into dust, or an empty stack */
  ItemStack gemstones$getGrindingGem();
}
