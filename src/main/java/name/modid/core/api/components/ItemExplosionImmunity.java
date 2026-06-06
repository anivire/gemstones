package name.modid.core.api.components;

import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.types.EventType;
import net.minecraft.item.ItemStack;

public final class ItemExplosionImmunity {
  private ItemExplosionImmunity() {
  }

  public static boolean isExplosionImmuneStack(ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return false;
    }

    return resolveExplosionImmunity(
        stack.getOrDefault(ComponentsRegistry.EXPLOSION_IMMUNE, false),
        hasExplosionImmuneGemstoneModifier(stack));
  }

  static boolean resolveExplosionImmunity(boolean hasComponent, boolean hasGemstoneModifier) {
    return hasComponent || hasGemstoneModifier;
  }

  private static boolean hasExplosionImmuneGemstoneModifier(ItemStack stack) {
    return !ModifierGatheringHelper.getModifiersByEventType(stack, EventType.ITEM_EXPLOSION_IMMUNE).isEmpty();
  }
}
