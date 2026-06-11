package name.modid.core.content.items;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import name.modid.Gemstones;
import name.modid.core.api.components.ComponentsRegistry;
import name.modid.core.api.components.PolishingComponent;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig;
import name.modid.core.api.modifiers.helpers.ModifierHelper;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
import name.modid.core.api.modifiers.types.ModifierItemCategory;
import name.modid.core.api.tooltips.QualityTooltipData;
import name.modid.core.api.tooltips.TooltipHelper;
import name.modid.core.api.tooltips.TooltipHelper.Icons;
import name.modid.core.api.tooltips.TooltipHelper.InlineIcons;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class GemstoneItem extends Item {
  protected GemstoneType gemstoneType;
  protected GemstoneQuality rarityType;

  public GemstoneItem(Settings settings, GemstoneType gemstoneType, GemstoneQuality rarityType) {
    super(settings);
    this.gemstoneType = gemstoneType;
    this.rarityType = rarityType;
  }

  public GemstoneType getType() {
    return this.gemstoneType;
  }

  public GemstoneQuality getRarityType() {
    return this.rarityType;
  }

  @Override
  public Optional<TooltipData> getTooltipData(ItemStack stack) {
    GemstoneItem gemstoneItem = (GemstoneItem) stack.getItem();
    List<GemstoneQuality> qualities = getDisplayedQualities(gemstoneItem.getRarityType(), isAmplifierGemstone(stack));
    return Optional.of(new QualityTooltipData(qualities));
  }

  @Override
  public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
    GemstoneItem gemstoneItem = (GemstoneItem) stack.getItem();
    GemstoneType gemstoneType = gemstoneItem.getType();
    Map<ModifierItemCategory, Map<GemstoneQuality, GemstoneModifier>> gemstoneModifiers = new LinkedHashMap<>(
        ModifierHelper.getGemstoneModifiers(gemstoneType, stack.getItem()));
    boolean isAmplifierGemstone = gemstoneModifiers.values().stream()
        .flatMap(rarityMap -> rarityMap.values().stream())
        .anyMatch(modifier -> modifier.getConfig() instanceof ModifierConfig.AmplifierConfig);

    if (!gemstoneModifiers.isEmpty()) {
      tooltip.add(Text.empty());
      tooltip.add(Text.translatable("tooltip.gemstones.gemstone_bonus").formatted(Formatting.GRAY));
    }

    List<ModifierItemCategory> modifierOrder = Arrays.asList(
        ModifierItemCategory.ALL,
        ModifierItemCategory.MELEE,
        ModifierItemCategory.RANGED,
        ModifierItemCategory.TOOLS,
        ModifierItemCategory.ARMOR);

    gemstoneModifiers.entrySet().stream()
        .sorted(Comparator.comparingInt(entry -> modifierOrder.indexOf(entry.getKey())))
        .forEachOrdered(entry -> {
          Map<GemstoneQuality, GemstoneModifier> rarityMap = entry.getValue();
          GemstoneModifier modifier = rarityMap.get(gemstoneItem.getRarityType());

          if (modifier != null && gemstoneType != GemstoneType.LOCKED && gemstoneType != GemstoneType.EMPTY) {
            tooltip.add(modifier.getTooltipText(gemstoneItem.getRarityType(), true));
          }
        });

    if (gemstoneItem.getRarityType() == GemstoneQuality.MYTHIC) {
      MutableText iconInfo = Text.literal(InlineIcons.INFO.getSymbol())
          .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
          .formatted(Formatting.WHITE);
      MutableText actionStart = Text
          .translatable("tooltip.gemstones.mythic_quality_warning")
          .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
          .formatted(Formatting.GRAY);

      tooltip.add(Text.empty());
      tooltip.add(iconInfo.append(actionStart));
    }

    if (isAmplifierGemstone) {
      MutableText iconInfo = Text.literal(InlineIcons.INFO.getSymbol())
          .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())))
          .formatted(Formatting.WHITE);
      MutableText actionStart = Text
          .translatable("tooltip.gemstones.amplifier_quality_warning")
          .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
          .formatted(Formatting.GRAY);
      MutableText infoIndent = Text.literal(InlineIcons.INFO_INDENT.getSymbol())
          .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())));
      MutableText boostedValues = Text
          .translatable("tooltip.gemstones.amplifier_quality_values")
          .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
          .formatted(Formatting.GRAY);

      tooltip.add(Text.empty());
      tooltip.add(iconInfo.append(actionStart));
      tooltip.add(infoIndent.append(boostedValues));
    }

    PolishingComponent polishing = stack.get(ComponentsRegistry.POLISHING);
    if (polishing != null) {
      int totalStages = gemstoneItem.getRarityType().getPolishStages();
      int currentStage = Math.min(polishing.completedStages() + 1, totalStages);
      int stagePercent = polishing.stageDuration() <= 0
          ? 0
          : polishing.ticksInStage() * 100 / polishing.stageDuration();

      tooltip.add(Text.empty());
      tooltip.add(Text.translatable("tooltip.gemstones.polishing.progress",
          currentStage,
          totalStages,
          stagePercent)
          .formatted(Formatting.GOLD));
    }
  }

  private boolean isAmplifierGemstone(ItemStack stack) {
    GemstoneItem gemstoneItem = (GemstoneItem) stack.getItem();
    return ModifierHelper.getGemstoneModifiers(gemstoneItem.getType(), stack.getItem()).values().stream()
        .flatMap(rarityMap -> rarityMap.values().stream())
        .anyMatch(modifier -> modifier.getConfig() instanceof ModifierConfig.AmplifierConfig);
  }

  private static List<GemstoneQuality> getDisplayedQualities(GemstoneQuality quality, boolean amplifier) {
    return amplifier ? List.of(GemstoneQuality.AMPLIFIER, quality) : List.of(quality);
  }
}
