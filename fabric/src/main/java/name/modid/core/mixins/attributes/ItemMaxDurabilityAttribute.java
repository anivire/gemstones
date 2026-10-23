package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
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
    AttributeModifiersComponent attr = itemStack
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
    // The item's own durability is the base, so percent bonuses (Obsidian Shard) scale with the material
    double bonusDurability = ModifierUtils.getAttributeDelta(
        attr,
        AttributesRegistry.MAX_DURABILITY_ATTRIBUTE,
        baseMax);

    if (bonusDurability != 0) {
      cir.setReturnValue(Math.max(1, baseMax + (int) Math.round(bonusDurability)));
    } else {
      cir.setReturnValue(cir.getReturnValue());
    }
  }
}
