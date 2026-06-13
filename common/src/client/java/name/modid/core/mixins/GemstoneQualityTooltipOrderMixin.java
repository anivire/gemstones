package name.modid.core.mixins;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.content.items.GemstoneItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

// Workaround for gemstones quality bg
@Mixin(ItemStack.class)
public class GemstoneQualityTooltipOrderMixin {
  private static final String MOD_NAME_TRANSLATION_KEY = "item_group.gemstones";

  @Inject(method = "getTooltip", at = @At("RETURN"))
  private void gemstones$moveModNameBelowQualityTooltip(Item.TooltipContext context, @Nullable PlayerEntity player,
      TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
    ItemStack stack = (ItemStack) (Object) this;
    if (stack.getItem() instanceof GemstoneItem) {
      moveModNameLineToEnd(cir.getReturnValue());
    }
  }

  private static void moveModNameLineToEnd(List<Text> text) {
    if (text.isEmpty()) {
      return;
    }

    Text modNameLine = Text.translatable(MOD_NAME_TRANSLATION_KEY).formatted(Formatting.BLUE);
    if (text.size() > 1 && isModNameLine(text.get(1))) {
      Text line = text.remove(1);

      if (!containsSameLine(text, line)) {
        text.add(line);
      }
      return;
    }

    if (!containsSameLine(text, modNameLine)) {
      text.add(modNameLine);
    }
  }

  private static boolean containsSameLine(List<Text> text, Text line) {
    String lineString = line.getString();
    for (Text currentLine : text) {
      if (currentLine.getString().equals(lineString)) {
        return true;
      }
    }

    return false;
  }

  private static boolean isModNameLine(Text line) {
    return line.getString().equals(Text.translatable(MOD_NAME_TRANSLATION_KEY).getString());
  }
}
