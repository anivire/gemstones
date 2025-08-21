package name.modid.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Mixin(BrewingStandBlockEntity.class)
public interface BrewingStandAccessor {
  @Accessor("inventory")
  DefaultedList<ItemStack> getInventory();
}