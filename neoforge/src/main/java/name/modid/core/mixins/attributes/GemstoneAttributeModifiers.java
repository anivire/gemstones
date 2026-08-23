package name.modid.core.mixins.attributes;

import java.util.function.BiConsumer;

import name.modid.core.api.components.ComponentsRegistry;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class GemstoneAttributeModifiers {

  @Inject(method = "applyAttributeModifier", at = @At("TAIL"))
  private void gemstones$appendTooltipAttributeModifiers(
      AttributeModifierSlot slot,
      BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> consumer,
      CallbackInfo ci) {
    ItemStack stack = (ItemStack) (Object) this;
    AttributeModifiersComponent modifiers = stack.get(ComponentsRegistry.gemstoneAttributeModifiers());

    if (modifiers != null) {
      modifiers.applyModifiers(slot, consumer);
    }
  }
}
