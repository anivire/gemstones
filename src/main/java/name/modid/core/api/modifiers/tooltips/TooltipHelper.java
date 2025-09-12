package name.modid.core.api.modifiers.tooltips;

import java.util.ArrayList;
import java.util.List;

import name.modid.Gemstones;
import name.modid.core.api.components.Gemstone;
import name.modid.core.api.modifiers.GemstoneQuality;
import name.modid.core.api.modifiers.GemstoneType;
import name.modid.core.api.modifiers.helpers.ModifierHelper;
import name.modid.core.api.modifiers.impl.GemstoneModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class TooltipHelper {
  public enum Icons {
    SOCKET("gemstone_sockets_font"),
    INLINE_GEMSTONE("gemstone_inline_font"),
    INLINE("icons_font"),
    QUALITY("quality_font");

    private final String path;

    Icons(String path) {
      this.path = path;
    }

    public String getPath() {
      return path;
    }
  }

  public enum InlineIcons {
    HALF_HEART("\uE001"),
    BLEEDING("\uE002"),
    GUARDIAN_SMITE("\uE003"),
    HALF_EXTRA_HEART("\uE004"),
    TIDE("\uE005"),
    ARROW_UP("\uE006"),
    LIGHTNING_STRIKE("\uE007"),
    TORRENT("\uE008"),
    HARVEST_MARK("\uE009"),
    SLOWNESS("\uE010"),
    STUNNED("\uE011"),
    ARROW_DOWN("\uE012"),
    LIFESTEAL("\uE013"),
    MOUSE_RMB("\uE014"),
    MOUSE_LMB("\uE015"),
    SHIFT("\uE016"),
    LOCKED("\uE017"),
    EMPTY("\uE018");

    private final String symbol;

    InlineIcons(String symbol) {
      this.symbol = symbol;
    }

    public String getSymbol() {
      return symbol;
    }

    public MutableText asText() {
      return Text.literal(symbol)
          .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())));
    }
  }

  private static Formatting getSlotColor(GemstoneType gemType) {
    return switch (gemType) {
      case EMPTY -> Formatting.DARK_GRAY;
      case LOCKED -> Formatting.DARK_GRAY;
      default -> Formatting.WHITE;
    };
  }

  private static String getSlotText(GemstoneType gemType) {
    return switch (gemType) {
      case LOCKED -> "Locked slot";
      case EMPTY -> "Empty slot";
      default -> "unknown";
    };
  }

  private static Text getGemstoneSprite(GemstoneType gemstoneType) {
    return Text.literal(gemstoneType.getGemstoneLiteral())
        .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.SOCKET.getPath())));
  }

  public static MutableText getGemstoneQualitySprite(GemstoneQuality rarityType) {
    return Text.literal(rarityType.getRarityLiteral())
        .styled(style -> style.withFont(Identifier.of(Gemstones.MOD_ID, Icons.QUALITY.getPath())))
        .formatted(Formatting.WHITE);
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
      GemstoneType gemstoneType = gemstones[i].gemstoneType();
      GemstoneQuality gemRarity = gemstones[i].GemstoneQualityType();

      if (gemstoneType != GemstoneType.LOCKED && gemstoneType != GemstoneType.EMPTY) {
        try {
          GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(
              gemstoneType,
              gemRarity,
              itemStack.getItem());
          rows.add(modifier.getTooltipText(gemRarity, false));
        } catch (NullPointerException e) {
          MutableText prefix = Text.translatable("tooltip.gemstones.without_type").formatted(Formatting.DARK_GRAY);
          rows.add(prefix.append(Text.literal("Undefined modifier").formatted(Formatting.RED)));
        }
      } else {
        MutableText prefix = Text.literal(gemstoneType.getGemstoneLiteral())
            .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE_GEMSTONE.getPath())))
            .formatted(Formatting.DARK_GRAY).append(Text.literal(" > ").formatted(Formatting.DARK_GRAY)
                .styled(style -> style.withFont(Style.DEFAULT_FONT_ID)));

        MutableText gemstoneSlot = Text.literal(TooltipHelper.getSlotText(gemstones[i].gemstoneType()))
            .formatted(TooltipHelper.getSlotColor(gemstones[i].gemstoneType()))
            .styled(style -> style.withFont(Style.DEFAULT_FONT_ID));

        rows.add(prefix.append(gemstoneSlot));
      }
    }

    return rows;
  }
}
