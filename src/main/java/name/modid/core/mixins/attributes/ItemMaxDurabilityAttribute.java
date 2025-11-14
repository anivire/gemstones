package name.modid.core.mixins.attributes;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class ItemMaxDurabilityAttribute {
  @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
  private void addDurabilityBonus(CallbackInfoReturnable<Integer> cir) {
    ItemStack itemStack = (ItemStack) (Object) this;
    int baseMax = cir.getReturnValueI();
    float bonusDurability = 0;

    AttributeModifiersComponent attr = itemStack
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
    List<AttributeModifiersComponent.Entry> mod = attr.modifiers();

    for (AttributeModifiersComponent.Entry m : mod) {
      if (m.attribute() == AttributesRegistry.MAX_DURABILITY_ATTRIBUTE) {
        bonusDurability += (float) m.modifier().value();
      }
    }

    if (bonusDurability != 0) {
      cir.setReturnValue(baseMax + (int) bonusDurability);
    } else {
      cir.setReturnValue(cir.getReturnValue());
    }
  }
}
