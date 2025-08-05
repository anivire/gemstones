package name.modid.helpers.attributes;

import name.modid.Gemstones;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class AttributeRegistrationHelper {
  public static final EntityAttribute PULL_SPEED =
      new ClampedEntityAttribute("attribute.name.generic.pull_speed", 1.0, 0.1, 10.0)
          .setTracked(true);

  public static final EntityAttribute CRIT_DAMAGE =
      new ClampedEntityAttribute("attribute.name.generic.crit_damage", 1.0, 0.1, 100.0)
          .setTracked(true);

  public static RegistryEntry<EntityAttribute> PULL_SPEED_ATTRIBUTE;
  public static RegistryEntry<EntityAttribute> CRIT_DAMAGE_ATTRIBUTE;

  public static void initialize() {
    PULL_SPEED_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE,
        Identifier.of(Gemstones.MOD_ID, "draw_speed"), PULL_SPEED);

    CRIT_DAMAGE_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE,
        Identifier.of(Gemstones.MOD_ID, "crit_damage"), CRIT_DAMAGE);
  }
}
