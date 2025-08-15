package name.modid.helpers.tooltips;

import java.util.ArrayList;
import java.util.List;
import name.modid.Gemstones;
import name.modid.helpers.components.Gemstone;
import name.modid.helpers.modifiers.GemstoneModifier;
import name.modid.helpers.modifiers.ModifierHelper;
import name.modid.helpers.types.GemstoneRarity;
import name.modid.helpers.types.GemstoneType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class GemstoneTooltipHelper {
  public static Formatting getGemstoneColor(GemstoneType gemType) {
    return switch (gemType) {
      case EMPTY -> Formatting.DARK_GRAY;
      case RUBY -> Formatting.RED;
      case CELESTINE -> Formatting.BLUE;
      case TOPAZ -> Formatting.GOLD;
      case SAPPHIRE -> Formatting.DARK_BLUE;
      case AQUAMARINE -> Formatting.BLUE;
      case OBSIDIAN_SHARD -> Formatting.DARK_PURPLE;
      default -> Formatting.GRAY;
    };
  }

  public static String getSlotText(GemstoneType gemType) {
    return switch (gemType) {
      case LOCKED -> "Locked slot";
      case EMPTY -> "Empty slot";
      case RUBY -> "Ruby";
      case CELESTINE -> "Celestine";
      case SAPPHIRE -> "Sapphire";
      case TOPAZ -> "Topaz";
      case AQUAMARINE -> "Aquamarine";
      case OBSIDIAN_SHARD -> "Obsidian Shard";
      default -> "unknown";
    };
  }

  public static Text getGemstoneSprite(GemstoneType gemType) {
    return Text.literal(GemstoneType.getGemstoneLiteral(gemType))
        .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "gemstone_sprite_font")));
  }

  public static Text getGemstoneRaritySprite(GemstoneRarity rarityType) {
    return Text.literal(GemstoneRarity.getRarityLiteral(rarityType))
        .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "rarity_sprite_font")));
  }

  public static Text getGemstoneSocketedRow(Gemstone[] gemstones) {
    MutableText row = Text.empty();
    for (Gemstone gemstoneSlot : gemstones) {
      row.append(getGemstoneSprite(gemstoneSlot.gemstoneType()));
    }
    return row;
  }

  public static List<Text> getItemGemstoneBonusesRows(Gemstone[] gemstones, ItemStack itemStack) {
    List<Text> rows = new ArrayList<>();

    for (int i = 0; i < gemstones.length; i++) {
      GemstoneType gemType = gemstones[i].gemstoneType();
      GemstoneRarity gemRarity = gemstones[i].gemstoneRarityType();

      if (gemType != GemstoneType.LOCKED && gemType != GemstoneType.EMPTY) {
        GemstoneModifier modifier =
            ModifierHelper.getGemstoneModifierForItem(gemType, itemStack.getItem());

        rows.add(modifier.getTooltipString(gemRarity, false));
      } else {
        MutableText symbol =
            Text.translatable("tooltip.gemstones.without_type").formatted(Formatting.GRAY);
        MutableText gemstoneSlot = Text
            .translatable(String.format("tooltip.gemstones.gemstone_slots.%d", i + 1),
                GemstoneTooltipHelper.getSlotText(gemstones[i].gemstoneType()))
            .formatted(GemstoneTooltipHelper.getGemstoneColor(gemstones[i].gemstoneType()));

        rows.add(symbol.append(gemstoneSlot));
      }
    }

    return rows;
  }
}
