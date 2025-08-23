package name.modid.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import name.modid.helpers.GemstoneSocketingHelper;
import name.modid.helpers.attributes.AttributeRegistrationHelper;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.modifiers.tooltips.GemstoneTooltipHelper;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
  @Inject(method = "<init>(Lnet/minecraft/registry/entry/RegistryEntry;ILnet/minecraft/component/ComponentChanges;)V", at = @At("TAIL"))
  private void onConstructWithChanges(RegistryEntry<Item> item, int count, ComponentChanges changes,
      CallbackInfo ci) {
    ItemStack itemStack = (ItemStack) (Object) this;
    GemstoneSocketingHelper.initItemSlots(itemStack, item.value());
  }

  @Inject(method = "<init>(Lnet/minecraft/registry/entry/RegistryEntry;I)V", at = @At("TAIL"))
  private void onConstruct(RegistryEntry<Item> item, int count, CallbackInfo ci) {
    ItemStack itemStack = (ItemStack) (Object) this;
    GemstoneSocketingHelper.initItemSlots(itemStack, item.value());
  }

  @Inject(method = "<init>(Lnet/minecraft/registry/entry/RegistryEntry;)V", at = @At("TAIL"))
  private void onConstructSimple(RegistryEntry<Item> item, CallbackInfo ci) {
    ItemStack itemStack = (ItemStack) (Object) this;
    GemstoneSocketingHelper.initItemSlots(itemStack, item.value());
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

  @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
  private void tooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type,
      CallbackInfoReturnable<List<Text>> cir, List<Text> tooltip) {
    ItemStack itemStack = (ItemStack) (Object) this;

    if (GemstoneSocketingHelper.isItemValid(itemStack.getItem())
        && GemstoneSocketingHelper.isGemstonesExists(itemStack)) {
      Gemstone[] gemstones = GemstoneSocketingHelper.getGemstones(itemStack);

      if (gemstones == null || gemstones.length == 0) {
        return;
      }

      // Empty rows for proper gemstones sprite visibility
      tooltip.add(Text.empty());
      tooltip.add(GemstoneTooltipHelper.getGemstoneSocketedRow(gemstones));
      tooltip.add(Text.empty());
      tooltip.add(Text.empty());
      tooltip.add(Text.translatable("tooltip.gemstones.gemstone_slots_gemstones_category")
          .formatted(Formatting.GRAY));
      tooltip.addAll(GemstoneTooltipHelper.getItemGemstoneBonusesRows(gemstones, itemStack));
    }
  }
}
