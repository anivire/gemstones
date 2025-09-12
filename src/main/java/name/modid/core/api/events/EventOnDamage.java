package name.modid.core.api.events;

import java.util.ArrayList;

import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.impl.categories.ModifierOnDamage;
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
      if (armorItem != null && GemstoneSlotHelper.isGemstonesExists(armorItem)) {
        allModifiersOnDamage.addAll(ModifierGatheringHelper.getOnDamageModifiers(armorItem));
      }
    }

    if (!allModifiersOnDamage.isEmpty()) {
      GemstoneSlotHelper.applyOnDamageModifiers(allModifiersOnDamage, entity, world);
    }
  }
}
