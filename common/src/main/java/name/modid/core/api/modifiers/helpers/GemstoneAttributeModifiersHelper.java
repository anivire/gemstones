package name.modid.core.api.modifiers.helpers;

import name.modid.core.api.components.ComponentsRegistry;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.ItemStack;

public final class GemstoneAttributeModifiersHelper {

  private GemstoneAttributeModifiersHelper() {
  }

  public static AttributeModifiersComponent getEffectiveModifiers(ItemStack itemStack) {
    AttributeModifiersComponent gemstoneModifiers = itemStack.get(ComponentsRegistry.gemstoneAttributeModifiers());
    return gemstoneModifiers == null ? AttributeModifiersComponent.DEFAULT : gemstoneModifiers;
  }
}
