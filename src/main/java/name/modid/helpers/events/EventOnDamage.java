package name.modid.helpers.events;

import java.util.ArrayList;

import name.modid.helpers.GemstoneSocketingHelper;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.modifierTypes.ModifierOnDamage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class EventOnDamage {
  public static void setupEvent(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken,
      boolean blocked) {
    ServerWorld world = (ServerWorld) entity.getWorld();
    ArrayList<ModifierOnDamage> allModifiersOnDamage = new ArrayList<>();

    for (ItemStack armorItem : entity.getArmorItems()) {
      if (armorItem != null && GemstoneSocketingHelper.isGemstonesExists(armorItem)) {
        allModifiersOnDamage.addAll(ModifierHelper.getOnDamageModifiers(armorItem));
      }
    }

    if (!allModifiersOnDamage.isEmpty()) {
      GemstoneSocketingHelper.applyOnDamageModifiers(allModifiersOnDamage, entity, world);
    }
  }
}
