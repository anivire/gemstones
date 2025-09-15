package name.modid.core.api.events;

import java.util.ArrayList;

import name.modid.core.api.modifiers.ModifierManagerLegacy;
import name.modid.core.api.modifiers.categories.ModifierOnHitMelee;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

public class EventOnHitMelee {
  public static void setupEvent(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken,
      boolean blocked) {
    ItemStack itemStack = source.getWeaponStack();
    ArrayList<ModifierOnHitMelee> modifiers = ModifierGatheringHelper.getOnHitMeleeModifiers(itemStack);

    if (modifiers.isEmpty()) {
      return;
    }

    ModifierManagerLegacy.applyOnHitMeleeModifiers(modifiers, itemStack, entity, source, damageTaken, damageTaken,
        blocked);
  }
}
