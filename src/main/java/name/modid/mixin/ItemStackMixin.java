package name.modid.mixin;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import name.modid.helpers.ItemGemstoneHelper;
import name.modid.helpers.attributes.AttributeRegistrationHelper;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
  // @Inject(
  // method =
  // "<init>(Lnet/minecraft/registry/entry/RegistryEntry;ILnet/minecraft/component/ComponentChanges;)V",
  // at = @At("TAIL"))
  // private void onConstruct(RegistryEntry<?> item, int count, ComponentChanges changes,
  // CallbackInfo ci) {
  // ItemStack itemStack = (ItemStack) (Object) this;
  // if (item instanceof RegistryEntry.Reference<?> reference
  // && reference.value() instanceof Item actualItem) {
  // ItemGemstoneHelper.initItemSlots(itemStack, actualItem);
  // }
  // }

  @Inject(
      method = "<init>(Lnet/minecraft/registry/entry/RegistryEntry;ILnet/minecraft/component/ComponentChanges;)V",
      at = @At("TAIL"))
  private void onConstructWithChanges(RegistryEntry<Item> item, int count, ComponentChanges changes,
      CallbackInfo ci) {
    ItemStack itemStack = (ItemStack) (Object) this;
    ItemGemstoneHelper.initItemSlots(itemStack, item.value());
  }

  @Inject(method = "<init>(Lnet/minecraft/registry/entry/RegistryEntry;I)V", at = @At("TAIL"))
  private void onConstruct(RegistryEntry<Item> item, int count, CallbackInfo ci) {
    ItemStack itemStack = (ItemStack) (Object) this;
    ItemGemstoneHelper.initItemSlots(itemStack, item.value());
  }

  @Inject(method = "<init>(Lnet/minecraft/registry/entry/RegistryEntry;)V", at = @At("TAIL"))
  private void onConstructSimple(RegistryEntry<Item> item, CallbackInfo ci) {
    ItemStack itemStack = (ItemStack) (Object) this;
    ItemGemstoneHelper.initItemSlots(itemStack, item.value());
  }

  @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
  private void addDurabilityBonus(CallbackInfoReturnable<Integer> cir) {
    ItemStack itemStack = (ItemStack) (Object) this;
    int baseMax = cir.getReturnValueI();
    float bonusDurability = 0;

    AttributeModifiersComponent attr = itemStack
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
    List<AttributeModifiersComponent.Entry> mod = attr.modifiers();

    for (AttributeModifiersComponent.Entry m : mod) {
      if (m.attribute() == AttributeRegistrationHelper.MAX_DURABILITY_ATTRIBUTE) {
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
