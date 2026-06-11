package name.modid.core.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import name.modid.Gemstones;
import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.api.modifiers.helpers.ModifierHelper;
import name.modid.core.api.modifiers.helpers.BoostHelper;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.tooltips.TooltipHelper;
import name.modid.core.api.tooltips.TooltipHelper.Icons;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import name.modid.core.content.items.GemstoneItem;
import name.modid.core.content.items.registries.GemstonesRegistry;
import net.minecraft.client.gui.screen.Screen;
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
      tooltip.addAll(getItemGemstoneBonusesRows(gemstones, itemStack));

    }
  }

  private List<Text> getItemGemstoneBonusesRows(GemstoneComponent[] gemstones, ItemStack item) {
    List<Text> rows = new ArrayList<>();
    boolean isShiftPressed = Screen.hasShiftDown();

    int i = 0;
    while (i < gemstones.length) {
      GemstoneComponent slot = gemstones[i];
      GemstoneType type = slot.gemstoneType();
      GemstoneQuality quality = slot.gemstoneQualityType();

      if (type == GemstoneType.EMPTY
          || type == GemstoneType.LOCKED) {
        int count = 1;
        int j = i + 1;

        while (j < gemstones.length && gemstones[j].gemstoneType() == type) {
          count++;
          j++;
        }

        String iconSymbol = (type == GemstoneType.EMPTY) ? InlineIcons.EMPTY.getSymbol()
            : InlineIcons.LOCKED.getSymbol();
        String countPrefix = (count > 1 ? count + "x " : "");
        Text slotText = Text.literal(countPrefix)
            .append(TooltipHelper.safeTranslatable(TooltipHelper.getSlotText(type)))
            .formatted(TooltipHelper.getSlotColor(type))
            .styled(s -> s.withFont(Style.DEFAULT_FONT_ID));

        rows.add(
            TooltipHelper.makeRow(iconSymbol, Icons.INLINE.getPath(), slotText, Optional.empty()));

        i = j;

        continue;
      }

      GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(type, quality, item.getItem());
      GemstoneModifier boostedModifier = modifier == null
          ? null
          : BoostHelper.applyBoost(gemstones, i, item.getItem(), modifier);

      if (isShiftPressed) {
        List<Item> gemstonesByType = GemstonesRegistry.getGemstonesByType(type);
        Optional<Item> found = gemstonesByType.stream()
            .filter(x -> x instanceof GemstoneItem gemstone && gemstone.getRarityType() == quality)
            .findFirst();
        MutableText icon = Text.literal(type.getGemstoneLiteral())
            .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE_GEMSTONE.getPath())))
            .formatted(Formatting.WHITE);
        MutableText name = Text.literal("")
            .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
            .append(found.map(f -> f.getDefaultStack().toHoverableText())
                .orElse(TooltipHelper.safeTranslatable("tooltip.gemstones.gemstone.undefined")
                    .formatted(Formatting.RED)));
        MutableText q = Text.translatable(quality.getTranslationString())
            .formatted(quality.getQualityTextcolor() == null
                ? Formatting.WHITE
                : quality.getQualityTextcolor());

        rows.add(Text.empty()
            .append(icon)
            .append(" > ")
            .formatted(Formatting.DARK_GRAY)
            .append(name)
            .append(" ")
            .append(q));
      } else {
        if (boostedModifier != null) {
          rows.add(boostedModifier.getTooltipText(
              quality,
              false,
              boostedModifier == modifier ? null : modifier));
        } else {
          rows.add(TooltipHelper.makeRow(
              type.getGemstoneLiteral(),
              Icons.INLINE_GEMSTONE,
              TooltipHelper.safeTranslatable("tooltip.gemstones.modifier.undefined")
                  .formatted(Formatting.RED)
                  .styled(s -> s.withFont(Style.DEFAULT_FONT_ID)),
              Optional.of(true)));
        }
      }

      i++;
    }

    return rows;
  }
}
