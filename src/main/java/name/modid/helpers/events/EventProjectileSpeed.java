package name.modid.helpers.events;

import name.modid.helpers.attributes.AttributeRegistrationHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class EventProjectileSpeed {
  public static void setupEvent(Entity entity, ServerWorld world) {
    if (entity instanceof ProjectileEntity projectile) {
      if (projectile.getOwner() instanceof LivingEntity owner) {
        ItemStack itemStack = owner.getWeaponStack();
        AttributeModifiersComponent modifiersComponent = itemStack
            .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

        Entry modifierEntry = modifiersComponent.modifiers().stream()
            .filter(x -> x.attribute() == AttributeRegistrationHelper.PROJECTILE_SPEED_ATTRIBUTE).findFirst()
            .orElse(null);

        if (modifierEntry != null) {
          double totalValue = 0.0;
          for (Entry e : modifiersComponent.modifiers()) {
            if (e.attribute() == AttributeRegistrationHelper.PROJECTILE_SPEED_ATTRIBUTE) {
              totalValue += e.modifier().value();
            }
          }

          projectile.setVelocity(projectile.getVelocity().multiply(1.0 + totalValue));
        } else {
          projectile.setVelocity(projectile.getVelocity());
        }
      }
    }
  }
}
