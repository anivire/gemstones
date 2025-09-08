package name.modid.helpers.events;

import java.util.ArrayList;

import name.modid.helpers.GemstoneSocketingHelper;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.category.ModifierOnHitMelee;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

public class EventOnHitMelee {
  public static void setupEvent(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken,
      boolean blocked) {
    ItemStack itemStack = source.getWeaponStack();
    ArrayList<ModifierOnHitMelee> modifiers = ModifierHelper.getOnHitMeleeModifiers(itemStack);

    if (modifiers.isEmpty()) {
      return;
    }

    GemstoneSocketingHelper.applyOnHitMeleeModifiers(modifiers, itemStack, entity, source, damageTaken, damageTaken,
        blocked);
  }
}
