package name.modid.core.utils.tooltip;

import java.util.Map;
import java.util.Set;

import name.modid.Gemstones;
import name.modid.core.api.modifiers.config.ModifierConfig;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Decides how an attribute modifier is shown: its name, and its value as a plain
 * number or as a percent
 */
public final class AttributeValueFormat {
  // Attributes whose vanilla name is too vague in a gemstone tooltip
  private static final Map<Identifier, String> NAME_OVERRIDES = Map.of(
      Identifier.ofVanilla("generic.scale"), "tooltip.gemstones.attribute_name.player_scale");

  // Attributes where an ADD_VALUE modifier already means a percent:
  // - geode_drop_chance, knockback_resistance: the value is a 0..1 chance or resistance
  // - submerged_mining_speed: the value is a share of the normal mining speed (base 0.2 = 20%)
  // - oxygen_bonus: +N stretches breath time by N * 100% (air is lost with chance 1 / (bonus + 1))
  private static final Set<Identifier> PERCENT_VALUE_ATTRIBUTES = Set.of(
      Identifier.of(Gemstones.MOD_ID, "geode_drop_chance"),
      Identifier.ofVanilla("generic.knockback_resistance"),
      Identifier.ofVanilla("player.submerged_mining_speed"),
      Identifier.ofVanilla("generic.oxygen_bonus"));

  private AttributeValueFormat() {
  }

  public static MutableText name(ModifierConfig.AttributeConfig cfg) {
    String key = cfg.attribute().getKey()
        .map(k -> NAME_OVERRIDES.get(k.getValue()))
        .orElse(null);
    return Text.translatable(key != null ? key : cfg.attribute().value().getTranslationKey().toLowerCase());
  }

  public static boolean isPercent(ModifierConfig.AttributeConfig cfg) {
    return cfg.operation() != Operation.ADD_VALUE
        || cfg.attribute().getKey().map(key -> PERCENT_VALUE_ATTRIBUTES.contains(key.getValue())).orElse(false);
  }

  public static double toDisplay(ModifierConfig.AttributeConfig cfg, double value) {
    return isPercent(cfg) ? value * 100 : value;
  }

  public static String postfix(ModifierConfig.AttributeConfig cfg) {
    return isPercent(cfg) ? "%" : "";
  }
}
