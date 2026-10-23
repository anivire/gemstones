package name.modid.core.mixins.attributes;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.world.World;

// Gives the multishot mixins access to the vanilla projectile creation, ammo and durability rules
@Mixin(RangedWeaponItem.class)
public interface RangedWeaponItemInvoker {
  @Invoker("createArrowEntity")
  ProjectileEntity gemstones$createArrowEntity(
      World world,
      LivingEntity shooter,
      ItemStack weaponStack,
      ItemStack projectileStack,
      boolean critical);

  @Invoker("getWeaponStackDamage")
  int gemstones$getWeaponStackDamage(ItemStack projectileStack);

  @Invoker("load")
  static List<ItemStack> gemstones$load(ItemStack weaponStack, ItemStack projectileStack, LivingEntity shooter) {
    throw new AssertionError();
  }
}
