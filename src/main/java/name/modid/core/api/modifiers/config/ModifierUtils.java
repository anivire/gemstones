package name.modid.core.api.modifiers.config;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;

public class ModifierUtils {

  public static boolean proc(ServerWorld world, double chance) {
    return world.getRandom().nextDouble() < chance;
  }

  public static void applyStatusEffect(LivingEntity target, StatusEffectInstance effect) {
    if (target == null)
      return;
    target.addStatusEffect(effect);
  }

  public static void applyStatusEffectToTarget(ModifierContext ctx, RegistryEntry<StatusEffect> effect, int duration,
      int amplifier) {
    ctx.getTarget().addStatusEffect(new StatusEffectInstance(
        effect,
        duration * 20,
        amplifier));
  }

  public static double collectAttributeValuesFromArmor(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
    double v = 0;

    for (EquipmentSlot slot : EquipmentSlot.values()) {
      if (slot == EquipmentSlot.CHEST || slot == EquipmentSlot.FEET
          || slot == EquipmentSlot.HEAD || slot == EquipmentSlot.LEGS) {
        ItemStack itemStack = entity.getEquippedStack(slot);
        AttributeModifiersComponent modifiersComponent = itemStack
            .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

        Entry modifierEntry = modifiersComponent.modifiers().stream()
            .filter(x -> x.attribute() == attribute).findFirst()
            .orElse(null);

        if (modifierEntry != null) {
          for (Entry e : modifiersComponent.modifiers()) {
            if (e.attribute() == attribute) {
              v += e.modifier().value();
            }
          }
        }
      }
    }

    return v;
  }

  public static double collectAttributeValuesFromItem(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
    double v = 0;

    for (EquipmentSlot slot : EquipmentSlot.values()) {
      if (slot == EquipmentSlot.MAINHAND) {
        ItemStack itemStack = entity.getEquippedStack(slot);
        AttributeModifiersComponent modifiersComponent = itemStack
            .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

        Entry modifierEntry = modifiersComponent.modifiers().stream()
            .filter(x -> x.attribute() == attribute).findFirst()
            .orElse(null);

        if (modifierEntry != null) {
          for (Entry e : modifiersComponent.modifiers()) {
            if (e.attribute() == attribute) {
              v += e.modifier().value();
            }
          }
        }
      }
    }

    return v;
  }
}