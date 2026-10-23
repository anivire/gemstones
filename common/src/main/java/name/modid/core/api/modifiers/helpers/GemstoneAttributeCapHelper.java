package name.modid.core.api.modifiers.helpers;

import name.modid.Gemstones;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class GemstoneAttributeCapHelper {
  // Ruby armor: at most +20 HP from gemstones over the whole armor set
  private static final double MAX_GEMSTONE_HEALTH = 20.0;
  private static final Identifier MAX_HEALTH_CAP_ID = Identifier.of(Gemstones.MOD_ID, "max_health_cap");
  private static final EquipmentSlot[] ARMOR_SLOTS = {
      EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

  private GemstoneAttributeCapHelper() {
  }

  // Armor pieces add their modifiers separately, so the cap is a negative modifier on the player
  public static void updateMaxHealthCap(PlayerEntity player) {
    EntityAttributeInstance maxHealth = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
    if (maxHealth == null) {
      return;
    }

    double gemstoneHealth = 0.0;
    for (EquipmentSlot slot : ARMOR_SLOTS) {
      gemstoneHealth += getGemstoneFlatValue(player.getEquippedStack(slot), slot);
    }

    double excess = Math.max(0.0, gemstoneHealth - MAX_GEMSTONE_HEALTH);
    EntityAttributeModifier current = maxHealth.getModifier(MAX_HEALTH_CAP_ID);

    if (excess <= 0.0) {
      if (current != null) {
        maxHealth.removeModifier(MAX_HEALTH_CAP_ID);
      }
      return;
    }

    if (current != null && current.value() == -excess) {
      return;
    }

    maxHealth.removeModifier(MAX_HEALTH_CAP_ID);
    maxHealth.addTemporaryModifier(new EntityAttributeModifier(
        MAX_HEALTH_CAP_ID,
        -excess,
        EntityAttributeModifier.Operation.ADD_VALUE));
  }

  private static double getGemstoneFlatValue(ItemStack stack, EquipmentSlot slot) {
    if (stack.isEmpty()) {
      return 0.0;
    }

    AttributeModifiersComponent component = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT);
    double sum = 0.0;

    for (AttributeModifiersComponent.Entry entry : component.modifiers()) {
      if (entry.attribute().equals(EntityAttributes.GENERIC_MAX_HEALTH)
          && entry.slot().matches(slot)
          && entry.modifier().operation() == EntityAttributeModifier.Operation.ADD_VALUE
          && entry.modifier().id().getNamespace().equals(Gemstones.MOD_ID)) {
        sum += entry.modifier().value();
      }
    }

    return sum;
  }
}
