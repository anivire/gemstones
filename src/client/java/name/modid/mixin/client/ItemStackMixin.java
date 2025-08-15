package name.modid.mixin.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import name.modid.helpers.GemstoneSocketingHelper;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.modifiers.ModifierItemCaregory;
import name.modid.helpers.tooltips.GemstoneTooltipHelper;
import name.modid.helpers.types.GemstoneType;
import name.modid.items.gemstones.GemstoneItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
  @Inject(
      method = "getTooltip", at = @At(value = "INVOKE",
          target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER),
      locals = LocalCapture.CAPTURE_FAILHARD)
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
    } else if (itemStack.getItem() instanceof GemstoneItem) {
      ArrayList<Text> tooltipText = new ArrayList<>();
      GemstoneItem gemstoneItem = (GemstoneItem) itemStack.getItem();
      GemstoneType gemstoneType = gemstoneItem.getType();
      Map<ModifierItemCaregory, GemstoneModifier> gemstoneModifiers = new LinkedHashMap<>(
          ModifierHelper.getGemstoneModifiers(gemstoneType, itemStack.getItem()));

      tooltipText.add(GemstoneTooltipHelper.getGemstoneRaritySprite(gemstoneItem.getRarityType()));
      tooltipText.add(Text.empty());

      List<ModifierItemCaregory> modifierOrder = Arrays.asList(ModifierItemCaregory.MELEE,
          ModifierItemCaregory.RANGED, ModifierItemCaregory.TOOLS, ModifierItemCaregory.ARMOR);

      gemstoneModifiers.entrySet().stream()
          .sorted(Comparator.comparingInt(entry -> modifierOrder.indexOf(entry.getKey())))
          .forEachOrdered(entry -> {
            GemstoneModifier modifier = entry.getValue();

            if (gemstoneType != GemstoneType.LOCKED && gemstoneType != GemstoneType.EMPTY) {
              tooltipText.add(modifier.getTooltipString(gemstoneItem.getRarityType(), true));
            }
          });

      tooltip.addAll(tooltipText);
    }
  }
}
