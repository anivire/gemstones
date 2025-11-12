package name.modid.core.content.registries;

import name.modid.Gemstones;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class AttributesRegistry {
  public static RegistryEntry<EntityAttribute> PULL_SPEED_ATTRIBUTE;
  public static RegistryEntry<EntityAttribute> CRIT_DAMAGE_ATTRIBUTE;
  public static RegistryEntry<EntityAttribute> MAX_DURABILITY_ATTRIBUTE;
  public static RegistryEntry<EntityAttribute> EVASION_ATTRIBUTE;
  public static RegistryEntry<EntityAttribute> PROJECTILE_SPEED_ATTRIBUTE;
  public static RegistryEntry<EntityAttribute> ARROW_DAMAGE_ATTRIBUTE;
  public static RegistryEntry<EntityAttribute> PROJECTILE_COUNT_ATTRIBUTE;
  public static RegistryEntry<EntityAttribute> ARMOR_PIERCE_ATTRIBUTE;
  public static RegistryEntry<EntityAttribute> JUMP_COUNT_ATTRIBUTE;
  public static RegistryEntry<EntityAttribute> MAGIC_PIERCE_ATTRIBUTE;

  public static final EntityAttribute PULL_SPEED = new ClampedEntityAttribute("attribute.name.generic.pull_speed", 1.0,
      0.1, 1024.0)
      .setTracked(true);

  public static final EntityAttribute CRIT_DAMAGE = new ClampedEntityAttribute("attribute.name.generic.crit_damage",
      1.0, 0.1, 1024.0)
      .setTracked(true);

  public static final EntityAttribute MAX_DURABILITY = new ClampedEntityAttribute(
      "attribute.name.generic.max_durability", 10.0, 1.0, 1024.0)
      .setTracked(true);

  public static final EntityAttribute EVASION = new ClampedEntityAttribute(
      "attribute.name.generic.evasion", 1.0, 0.0, 99.0)
      .setTracked(true);

  public static final EntityAttribute PROJECTILE_SPEED = new ClampedEntityAttribute(
      "attribute.name.generic.projectile_speed", 1.0, 0.0, 1024.0)
      .setTracked(true);

  public static final EntityAttribute ARROW_DAMAGE = new ClampedEntityAttribute(
      "attribute.name.generic.arrow_damage", 1.0, 0.0, 1024.0)
      .setTracked(true);

  public static final EntityAttribute PROJECTILE_COUNT = new ClampedEntityAttribute(
      "attribute.name.generic.projectile_count", 1.0, 0.0, 10.0)
      .setTracked(true);

  public static final EntityAttribute ARMOR_PIERCE = new ClampedEntityAttribute(
      "attribute.name.generic.armor_pierce", 0.0, 0.0, 1024.0)
      .setTracked(true);

  public static final EntityAttribute JUMP_COUNT = new ClampedEntityAttribute(
      "attribute.name.generic.jump_count", 0.0, 0.0, 1024.0)
      .setTracked(true);

  public static void initialize() {
    PULL_SPEED_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE,
        Identifier.of(Gemstones.MOD_ID, "pull_speed"), PULL_SPEED);

    CRIT_DAMAGE_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE,
        Identifier.of(Gemstones.MOD_ID, "crit_damage"), CRIT_DAMAGE);

    MAX_DURABILITY_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE,
        Identifier.of(Gemstones.MOD_ID, "max_durability"), MAX_DURABILITY);

    EVASION_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE,
        Identifier.of(Gemstones.MOD_ID, "evasion"), EVASION);

    PROJECTILE_SPEED_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE,
        Identifier.of(Gemstones.MOD_ID, "projectile_speed"), PROJECTILE_SPEED);

    ARROW_DAMAGE_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE,
        Identifier.of(Gemstones.MOD_ID, "arrow_damage"), ARROW_DAMAGE);

    PROJECTILE_COUNT_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE,
        Identifier.of(Gemstones.MOD_ID, "projectile_count"), PROJECTILE_COUNT);

    ARMOR_PIERCE_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE,
        Identifier.of(Gemstones.MOD_ID, "armor_pierce"), ARMOR_PIERCE);

    JUMP_COUNT_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE,
        Identifier.of(Gemstones.MOD_ID, "jump_count"), JUMP_COUNT);
  }
}
