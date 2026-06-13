package name.modid.neoforge.client;

import com.mojang.datafixers.util.Either;
import java.util.List;

import name.modid.Gemstones;
import net.minecraft.registry.Registries;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class GemstonesNeoForgeClientEvents {
  private static final String MOD_NAME_TRANSLATION_KEY = "item_group.gemstones";

  private GemstonesNeoForgeClientEvents() {
  }

  public static void onItemTooltip(ItemTooltipEvent event) {
    if (Registries.ITEM.getId(event.getItemStack().getItem()).getNamespace().equals(Gemstones.MOD_ID)) {
      removeDuplicateModNameLines(event.getToolTip());
    }
  }

  public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
    if (Registries.ITEM.getId(event.getItemStack().getItem()).getNamespace().equals(Gemstones.MOD_ID)) {
      removeDuplicateModNameElements(event.getTooltipElements());
    }
  }

  private static void removeDuplicateModNameLines(List<Text> tooltip) {
    int lastModNameIndex = -1;
    int modNameCount = 0;

    for (int i = 0; i < tooltip.size(); i++) {
      if (isModNameLine(tooltip.get(i))) {
        lastModNameIndex = i;
        modNameCount++;
      }
    }

    if (modNameCount < 2) {
      return;
    }

    for (int i = tooltip.size() - 1; i >= 0; i--) {
      if (i != lastModNameIndex && isModNameLine(tooltip.get(i))) {
        tooltip.remove(i);
      }
    }
  }

  private static boolean isModNameLine(Text line) {
    return line.getString().equals(Text.translatable(MOD_NAME_TRANSLATION_KEY).getString());
  }

  private static void removeDuplicateModNameElements(List<? extends Either<StringVisitable, ?>> tooltipElements) {
    int lastModNameIndex = -1;
    int modNameCount = 0;

    for (int i = 0; i < tooltipElements.size(); i++) {
      Either<StringVisitable, ?> element = tooltipElements.get(i);
      if (element.left().isPresent() && isModNameElement(element.left().get())) {
        lastModNameIndex = i;
        modNameCount++;
      }
    }

    if (modNameCount < 2) {
      return;
    }

    for (int i = tooltipElements.size() - 1; i >= 0; i--) {
      Either<StringVisitable, ?> element = tooltipElements.get(i);
      if (i != lastModNameIndex && element.left().isPresent() && isModNameElement(element.left().get())) {
        tooltipElements.remove(i);
      }
    }
  }

  private static boolean isModNameElement(StringVisitable line) {
    return line.getString().equals(Text.translatable(MOD_NAME_TRANSLATION_KEY).getString());
  }
}
