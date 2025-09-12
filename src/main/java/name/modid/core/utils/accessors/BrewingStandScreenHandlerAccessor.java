package name.modid.core.utils.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.BrewingStandScreenHandler;

@Mixin(BrewingStandScreenHandler.class)
public interface BrewingStandScreenHandlerAccessor {
  @Accessor("inventory")
  Inventory getInventory();
}