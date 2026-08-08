package name.modid.core.api.modifiers.helpers;

import java.util.function.Function;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.ItemStack;

public final class GemstoneAttributeModifiersHelper {

  private static Function<ItemStack, AttributeModifiersComponent> effectiveProvider = GemstoneAttributeModifiersHelper::defaultEffectiveModifiers;

  private GemstoneAttributeModifiersHelper() {
  }

  public static void setEffectiveProvider(Function<ItemStack, AttributeModifiersComponent> provider) {
    if (provider != null) {
      effectiveProvider = provider;
    }
  }

  public static AttributeModifiersComponent getEffectiveModifiers(ItemStack itemStack) {
    return effectiveProvider.apply(itemStack);
  }

  private static AttributeModifiersComponent defaultEffectiveModifiers(ItemStack itemStack) {
    return itemStack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
  }
}
