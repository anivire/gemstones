package name.modid.core.content.events.misc;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class EventProjectileSpeed {
  private static final String SPEED_APPLIED_TAG = "gemstones_projectile_speed_applied";

  public static void setup(Entity entity, ServerWorld world) {
    // Entity add also fires on chunk load and dimension change, the speed must be applied only once.
    // Only weapon projectiles are affected: thrown pearls or Wither Guard skulls are not shot from the weapon
    if (!(entity instanceof PersistentProjectileEntity || entity instanceof FireworkRocketEntity)
        || !(entity instanceof ProjectileEntity projectile)
        || !(projectile.getOwner() instanceof LivingEntity owner)
        || projectile.getCommandTags().contains(SPEED_APPLIED_TAG)) {
      return;
    }

    projectile.addCommandTag(SPEED_APPLIED_TAG);

    ItemStack weaponStack = projectile instanceof PersistentProjectileEntity persistentProjectile
        ? persistentProjectile.getWeaponStack()
        : owner.getWeaponStack();

    if (weaponStack == null || weaponStack.isEmpty()) {
      return;
    }

    AttributeModifiersComponent modifiersComponent = weaponStack
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

    projectile.setVelocity(projectile.getVelocity().multiply(
        ModifierUtils.getAttributeMultiplier(
            modifiersComponent,
            AttributesRegistry.PROJECTILE_SPEED_ATTRIBUTE)));

    projectile.velocityDirty = true;
  }
}
