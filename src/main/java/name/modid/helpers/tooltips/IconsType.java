package name.modid.helpers.tooltips;

import name.modid.Gemstones;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public enum IconsType {
  ABSORPTION_HALF_HEART("\uE004"), HALF_HEART("\uE001"), BLEEDING("\uE002"), GUARDIAN_SMITE(
      "\uE003"), TIDE("\uE005"), BONUS_ARROW("\uE006"), LIGHTNING_BOLT("\uE007");

  private final String unicodeChar;
  private static final Identifier FONT_IDENTIFIER =
      Identifier.of(Gemstones.MOD_ID, "gemstone_sprite_icons");

  IconsType(String unicodeChar) {
    this.unicodeChar = unicodeChar;
  }

  public MutableText getIconText() {
    return Text.literal(this.unicodeChar).styled(style -> style.withFont(FONT_IDENTIFIER))
        .formatted(Formatting.WHITE);
  }
}
