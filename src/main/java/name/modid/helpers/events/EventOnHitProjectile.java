package name.modid.helpers.events;

import java.util.ArrayList;

import name.modid.helpers.GemstoneSocketingHelper;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.category.ModifierOnHitProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class EventOnHitProjectile {
  public static void setupEvent(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken,
      boolean blocked) {
    ItemStack itemStack = source.getWeaponStack();
    ArrayList<ModifierOnHitProjectile> modifiers = ModifierHelper.getOnHitProjectileModifiers(itemStack);

    if (modifiers.isEmpty()) {
      return;
    }

    if (entity instanceof LivingEntity target && source.getSource() instanceof ArrowEntity arrow) {
      if (entity.getWorld() instanceof ServerWorld serverWorld) {
        GemstoneSocketingHelper.applyOnHitProjectileModifiers(modifiers, itemStack, serverWorld, target.getPos(), arrow,
            target);
      }
    }
  }
}
