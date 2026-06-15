package name.modid.neoforge.client;

import com.mojang.datafixers.util.Either;
import java.util.List;

import name.modid.Gemstones;
import name.modid.core.api.tooltips.QualityTooltipData;
import net.minecraft.registry.Registries;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.item.tooltip.TooltipData;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class GemstonesNeoForgeClientEvents {
  private static final String MOD_NAME_TRANSLATION_KEY = "item_group.gemstones";
  private static final int QUALITY_TOOLTIP_INDEX = 1;

  private GemstonesNeoForgeClientEvents() {
  }

  public static void onItemTooltip(ItemTooltipEvent event) {
    if (Registries.ITEM.getId(event.getItemStack().getItem()).getNamespace().equals(Gemstones.MOD_ID)) {
      removeNonFooterModNameLines(event.getToolTip());
    }
  }

  public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
    if (Registries.ITEM.getId(event.getItemStack().getItem()).getNamespace().equals(Gemstones.MOD_ID)) {
      moveQualityTooltipElement(event.getTooltipElements());
      removeNonFooterModNameElements(event.getTooltipElements());
    }
  }

  private static void removeNonFooterModNameLines(List<Text> tooltip) {
    for (int i = tooltip.size() - 1; i >= 0; i--) {
      if (i != tooltip.size() - 1 && isModNameLine(tooltip.get(i))) {
        tooltip.remove(i);
      }
    }
  }

  private static boolean isModNameLine(Text line) {
    return line.getString().equals(Text.translatable(MOD_NAME_TRANSLATION_KEY).getString());
  }

  private static void moveQualityTooltipElement(List<Either<StringVisitable, TooltipData>> tooltipElements) {
    QualityTooltipData qualityTooltipData = null;

    for (int i = tooltipElements.size() - 1; i >= 0; i--) {
      Either<StringVisitable, TooltipData> element = tooltipElements.get(i);
      if (element.right().isPresent() && element.right().get() instanceof QualityTooltipData data) {
        qualityTooltipData = data;
        tooltipElements.remove(i);
      }
    }

    if (qualityTooltipData != null) {
      tooltipElements.add(Math.min(QUALITY_TOOLTIP_INDEX, tooltipElements.size()), Either.right(qualityTooltipData));
    }
  }

  private static void removeNonFooterModNameElements(List<Either<StringVisitable, TooltipData>> tooltipElements) {
    for (int i = tooltipElements.size() - 1; i >= 0; i--) {
      Either<StringVisitable, TooltipData> element = tooltipElements.get(i);
      if (i != tooltipElements.size() - 1 && element.left().isPresent() && isModNameElement(element.left().get())) {
        tooltipElements.remove(i);
      }
    }
  }

  private static boolean isModNameElement(StringVisitable line) {
    return line.getString().equals(Text.translatable(MOD_NAME_TRANSLATION_KEY).getString());
  }
}
