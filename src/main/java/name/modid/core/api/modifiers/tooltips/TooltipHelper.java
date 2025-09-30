package name.modid.core.api.modifiers.tooltips;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import name.modid.Gemstones;
import name.modid.core.api.components.GemstoneComponent;
import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.helpers.ModifierHelper;
import name.modid.core.api.modifiers.types.GemstoneQuality;
import name.modid.core.api.modifiers.types.GemstoneType;
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
          .styled(s -> s.withFont(Identifier.of(Gemstones.MOD_ID, Icons.INLINE.getPath())));
    }
  }

  private static Formatting getSlotColor(GemstoneType gemType) {
    return switch (gemType) {
      case EMPTY -> Formatting.DARK_GRAY;
      case LOCKED -> Formatting.RED;
      default -> Formatting.WHITE;
    };
  }

  private static String getSlotText(GemstoneType gemType) {
    return switch (gemType) {
      case LOCKED -> "tooltip.gemstones.gemstone_type.locked";
      case EMPTY -> "tooltip.gemstones.gemstone_type.empty";
      default -> "tooltip.gemstones.gemstone_type.undefined";
    };
  }

  private static Text getGemstoneSprite(GemstoneType gemstoneType) {
    return Text.literal(gemstoneType.getGemstoneLiteral())
        .styled(s -> s.withFont(Identifier.of(Gemstones.MOD_ID, Icons.SOCKET.getPath())));
  }

  public static MutableText getGemstoneQualitySprite(GemstoneQuality rarityType) {
    return Text.literal(rarityType.getRarityLiteral())
        .styled(s -> s.withFont(Identifier.of(Gemstones.MOD_ID, Icons.QUALITY.getPath())))
        .formatted(Formatting.WHITE);
  }

  public static Text getGemstoneSocketedRow(GemstoneComponent[] gemstones) {
    MutableText row = Text.empty();
    for (GemstoneComponent slot : gemstones) {
      row.append(getGemstoneSprite(slot.gemstoneType()));
    }
    return row;
  }

  public static List<Text> getItemGemstoneBonusesRows(GemstoneComponent[] gemstones, ItemStack item) {
    List<Text> rows = new ArrayList<>();

    for (GemstoneComponent slot : gemstones) {
      GemstoneType type = slot.gemstoneType();
      GemstoneQuality rarity = slot.gemstoneQualityType();

      if (type != GemstoneType.EMPTY && type != GemstoneType.LOCKED) {
        GemstoneModifier modifier = ModifierHelper.getGemstoneModifierForItem(type, rarity, item.getItem());

        if (modifier != null) {
          rows.add(modifier.getTooltipText(rarity, false));
        } else {
          String l = type == GemstoneType.EMPTY
              ? InlineIcons.EMPTY.getSymbol()
              : type == GemstoneType.LOCKED
                  ? InlineIcons.LOCKED.getSymbol()
                  : type.getGemstoneLiteral();

          Icons p = type != GemstoneType.EMPTY && type != GemstoneType.LOCKED
              ? Icons.INLINE_GEMSTONE
              : Icons.INLINE;

          rows.add(makeRow(l, p,
              Text.literal("Undefined modifier")
                  .formatted(Formatting.RED)
                  .styled(s -> s.withFont(Style.DEFAULT_FONT_ID)),
              Optional.of(true)));
        }
      } else {
        String iconSymbol = (type == GemstoneType.EMPTY)
            ? InlineIcons.EMPTY.getSymbol()
            : InlineIcons.LOCKED.getSymbol();

        String fontPath = (type == GemstoneType.EMPTY || type == GemstoneType.LOCKED)
            ? Icons.INLINE.getPath()
            : Icons.INLINE_GEMSTONE.getPath();

        Text slotText = Text.translatable(getSlotText(type))
            .formatted(getSlotColor(type))
            .styled(s -> s.withFont(Style.DEFAULT_FONT_ID));

        rows.add(makeRow(iconSymbol, fontPath, slotText, Optional.empty()));
      }
    }
    return rows;
  }

  private static MutableText makeRow(String icon, Icons fontIcon, Text content, Optional<Boolean> isGemstoneIcon) {
    return makeRow(icon, fontIcon.getPath(), content, isGemstoneIcon);
  }

  private static MutableText makeRow(String icon, String fontPath, Text content, Optional<Boolean> isGemstoneIcon) {
    Formatting iconColor = isGemstoneIcon.orElse(false) ? Formatting.WHITE : Formatting.DARK_GRAY;

    MutableText prefix = Text.literal(icon)
        .setStyle(Style.EMPTY.withFont(Identifier.of(Gemstones.MOD_ID, fontPath)))
        .formatted(iconColor)
        .append(Text.literal(" > ")
            .formatted(Formatting.DARK_GRAY)
            .styled(s -> s.withFont(Style.DEFAULT_FONT_ID)));

    return prefix.append(content);
  }
}