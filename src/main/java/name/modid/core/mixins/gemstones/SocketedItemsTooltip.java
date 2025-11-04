package name.modid.core.mixins.gemstones;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import name.modid.Gemstones;
import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.api.modifiers.tooltips.TooltipHelper;
import name.modid.core.api.modifiers.tooltips.TooltipHelper.Icons;
import name.modid.core.api.modifiers.tooltips.TooltipHelper.InlineIcons;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Mixin(ItemStack.class)
public class SocketedItemsTooltip {
  // TODO: move geodes and probably all other tooltips to client for SHIFT support
  @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
  private void tooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type,
      CallbackInfoReturnable<List<Text>> cir, List<Text> tooltip) {
    ItemStack itemStack = (ItemStack) (Object) this;

    if (GemstoneSlotHelper.isItemValid(itemStack.getItem())
        && GemstoneSlotHelper.isGemstonesExists(itemStack)) {
      GemstoneComponent[] gemstones = GemstoneSlotHelper.getGemstones(itemStack);

      if (gemstones == null || gemstones.length == 0) {
        return;
      }

      // Empty rows for proper gemstones sprite visibility
      tooltip.add(Text.empty());

      if (!Gemstones.ALT_STYLE) {
        tooltip.add(TooltipHelper.getGemstoneSocketedRow(gemstones));
        tooltip.add(Text.empty());
        tooltip.add(Text.empty());
      }

      tooltip.add(Text.translatable("tooltip.gemstones.category_name").formatted(Formatting.GRAY));
      tooltip.addAll(TooltipHelper.getItemGemstoneBonusesRows(gemstones, itemStack));

      MutableText iconInfo = Text.literal(InlineIcons.SHIFT.getSymbol())
          .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID,
              Icons.INLINE.getPath())))
          .formatted(Formatting.WHITE);
      MutableText arrowInfo = Text.literal(" > ")
          .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
          .formatted(Formatting.DARK_GRAY);
      MutableText actionInfo = Text.literal("Hold Shift to see ")
          .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
          .formatted(Formatting.YELLOW);
      MutableText keywordInfo = Text.literal("Gemstones Additional Info")
          .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
          .formatted(Formatting.GOLD);

      tooltip.addLast(Text.empty());
      tooltip.addLast(iconInfo.append(arrowInfo).append(actionInfo).append(keywordInfo));
    }
  }
}
