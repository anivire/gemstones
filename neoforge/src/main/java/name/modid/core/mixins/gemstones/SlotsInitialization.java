package name.modid.core.mixins.gemstones;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

@Mixin(ItemStack.class)
public abstract class SlotsInitialization {
  @Inject(method = "<init>(Lnet/minecraft/registry/entry/RegistryEntry;ILnet/minecraft/component/ComponentChanges;)V", at = @At("TAIL"))
  private void onConstructWithChanges(RegistryEntry<Item> item, int count, ComponentChanges changes,
      CallbackInfo ci) {
    ItemStack itemStack = (ItemStack) (Object) this;
    GemstoneSlotHelper.initializeSocketsIfEligible(itemStack, item.value());
  }

  @Inject(method = "<init>(Lnet/minecraft/registry/entry/RegistryEntry;I)V", at = @At("TAIL"))
  private void onConstruct(RegistryEntry<Item> item, int count, CallbackInfo ci) {
    ItemStack itemStack = (ItemStack) (Object) this;
    GemstoneSlotHelper.initializeSocketsIfEligible(itemStack, item.value());
  }

  @Inject(method = "<init>(Lnet/minecraft/registry/entry/RegistryEntry;)V", at = @At("TAIL"))
  private void onConstructSimple(RegistryEntry<Item> item, CallbackInfo ci) {
    ItemStack itemStack = (ItemStack) (Object) this;
    GemstoneSlotHelper.initializeSocketsIfEligible(itemStack, item.value());
  }
}
