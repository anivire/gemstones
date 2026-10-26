package name.modid.core.api.tooltips;

import java.util.ArrayList;
import java.util.List;

import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.helpers.BoostHelper;
import name.modid.core.api.modifiers.helpers.GemstoneSlotHelper;
import name.modid.core.api.modifiers.helpers.ModifierHelper;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.config.GemstonesClientConfig;
import name.modid.core.content.items.GemstoneItem;
import name.modid.core.content.items.registries.GemstonesRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class SocketsTooltip {
  private SocketsTooltip() {
  }

  public static void appendTo(ItemStack stack, List<Text> tooltip) {
    if (!GemstoneSlotHelper.isItemValid(stack.getItem()) || !GemstoneSlotHelper.isGemstonesExists(stack)) {
      return;
    }

    GemstoneComponent[] gemstones = GemstoneSlotHelper.getGemstones(stack);
    if (gemstones == null || gemstones.length == 0) {
      return;
    }

    if (!GemstonesClientConfig.get().showEmptySockets && !hasSocketedGemstone(gemstones)) {
      return;
    }

    tooltip.add(Text.empty());
    tooltip.add(Text.translatable("tooltip.gemstones.category_name").formatted(Formatting.GRAY));
    tooltip.addAll(buildRows(gemstones, stack.getItem()));
  }

  private static boolean hasSocketedGemstone(GemstoneComponent[] gemstones) {
    for (GemstoneComponent gemstone : gemstones) {
      if (gemstone.gemstoneType() != GemstoneType.EMPTY && gemstone.gemstoneType() != GemstoneType.LOCKED) {
        return true;
      }
    }

    return false;
  }

  private static List<Text> buildRows(GemstoneComponent[] gemstones, Item item) {
    List<Text> rows = new ArrayList<>();
    boolean isShiftPressed = Screen.hasShiftDown();

    int i = 0;
    while (i < gemstones.length) {
      GemstoneType type = gemstones[i].gemstoneType();

      // Consecutive empty or locked sockets collapse into a single "Nx" row
      if (type == GemstoneType.EMPTY || type == GemstoneType.LOCKED) {
        int next = i + 1;
        while (next < gemstones.length && gemstones[next].gemstoneType() == type) {
          next++;
        }

        rows.add(emptySocketRow(type, next - i));
        i = next;
        continue;
      }

      rows.add(isShiftPressed ? gemstoneNameRow(gemstones[i]) : modifierRow(gemstones, i, item));
      i++;
    }

    return rows;
  }

  private static Text emptySocketRow(GemstoneType type, int count) {
    return TooltipText.row(
        TooltipText.socketIcon(type),
        Text.literal(count > 1 ? count + "x " : "")
            .append(TooltipHelper.safeTranslatable(TooltipHelper.getSlotText(type)))
            .formatted(TooltipHelper.getSlotColor(type)));
  }

  private static Text gemstoneNameRow(GemstoneComponent slot) {
    GemstoneType type = slot.gemstoneType();
    GemstoneQuality quality = slot.gemstoneQualityType();

    Text name = GemstonesRegistry.getGemstonesByType(type).stream()
        .filter(x -> x instanceof GemstoneItem gemstone && gemstone.getRarityType() == quality)
        .findFirst()
        .map(x -> x.getDefaultStack().toHoverableText())
        .orElseGet(() -> TooltipHelper.safeTranslatable("tooltip.gemstones.gemstone.undefined")
            .formatted(Formatting.RED));
    Formatting qualityColor = quality.getQualityTextcolor() == null
        ? Formatting.WHITE
        : quality.getQualityTextcolor();

    return TooltipText.row(
        TooltipText.socketIcon(type),
        Text.empty()
            .append(name)
            .append(" ")
            .append(Text.translatable(quality.getTranslationString()).formatted(qualityColor)));
  }

  private static Text modifierRow(GemstoneComponent[] gemstones, int index, Item item) {
    GemstoneComponent slot = gemstones[index];
    GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(
        slot.gemstoneType(), slot.gemstoneQualityType(), item);

    if (modifier == null) {
      return TooltipText.row(
          TooltipText.socketIcon(slot.gemstoneType()),
          TooltipHelper.safeTranslatable("tooltip.gemstones.modifier.undefined").formatted(Formatting.RED));
    }

    GemstoneModifier boostedModifier = BoostHelper.applyBoost(gemstones, index, item, modifier);
    return boostedModifier.getTooltipText(
        slot.gemstoneQualityType(),
        false,
        boostedModifier == modifier ? null : modifier);
  }
}
