package name.modid.helpers;

import name.modid.Gemstones;
import name.modid.helpers.types.GemstoneRarityType;
import name.modid.helpers.types.GemstoneType;
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
      case LOCKED -> "Locked";
      case EMPTY -> "Empty";
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

  public static Text getGemstoneRaritySprite(GemstoneRarityType rarityType) {
    return Text.literal(GemstoneRarityType.getRarityLiteral(rarityType))
        .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, "rarity_sprite_font")));
  }
}
