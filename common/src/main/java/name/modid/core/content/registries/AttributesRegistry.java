package name.modid.core.content.registries;

import name.modid.Gemstones;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public class AttributesRegistry {
  private static final DeferredRegister<EntityAttribute> ATTRIBUTES = DeferredRegister.create(
      Gemstones.MOD_ID,
      RegistryKeys.ATTRIBUTE);

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

  public static final EntityAttribute GEODE_DROP_CHANCE = new ClampedEntityAttribute(
      "attribute.name.generic.geode_drop_chance", 0.0, 0.0, 1.0)
      .setTracked(false);

  public static final EntityAttribute INVULNERABILITY_FRAMES = new ClampedEntityAttribute(
      "attribute.name.generic.invulnerability_frames", 1.0, 0.0, 32.0)
      .setTracked(false);

  public static final EntityAttribute SWIM_SPEED = new ClampedEntityAttribute(
      "attribute.name.generic.swim_speed", 1.0, 0.0, 1024.0)
      .setTracked(true);

  private static final RegistrySupplier<EntityAttribute> PULL_SPEED_SUPPLIER = ATTRIBUTES.register("pull_speed",
      () -> PULL_SPEED);
  private static final RegistrySupplier<EntityAttribute> CRIT_DAMAGE_SUPPLIER = ATTRIBUTES.register("crit_damage",
      () -> CRIT_DAMAGE);
  private static final RegistrySupplier<EntityAttribute> MAX_DURABILITY_SUPPLIER = ATTRIBUTES.register("max_durability",
      () -> MAX_DURABILITY);
  private static final RegistrySupplier<EntityAttribute> EVASION_SUPPLIER = ATTRIBUTES.register("evasion",
      () -> EVASION);
  private static final RegistrySupplier<EntityAttribute> PROJECTILE_SPEED_SUPPLIER = ATTRIBUTES.register(
      "projectile_speed",
      () -> PROJECTILE_SPEED);
  private static final RegistrySupplier<EntityAttribute> ARROW_DAMAGE_SUPPLIER = ATTRIBUTES.register("arrow_damage",
      () -> ARROW_DAMAGE);
  private static final RegistrySupplier<EntityAttribute> PROJECTILE_COUNT_SUPPLIER = ATTRIBUTES.register(
      "projectile_count",
      () -> PROJECTILE_COUNT);
  private static final RegistrySupplier<EntityAttribute> ARMOR_PIERCE_SUPPLIER = ATTRIBUTES.register("armor_pierce",
      () -> ARMOR_PIERCE);
  private static final RegistrySupplier<EntityAttribute> JUMP_COUNT_SUPPLIER = ATTRIBUTES.register("jump_count",
      () -> JUMP_COUNT);
  private static final RegistrySupplier<EntityAttribute> GEODE_DROP_CHANCE_SUPPLIER = ATTRIBUTES.register(
      "geode_drop_chance",
      () -> GEODE_DROP_CHANCE);
  private static final RegistrySupplier<EntityAttribute> INVULNERABILITY_FRAMES_SUPPLIER = ATTRIBUTES.register(
      "invulnerability_frames",
      () -> INVULNERABILITY_FRAMES);
  private static final RegistrySupplier<EntityAttribute> SWIM_SPEED_SUPPLIER = ATTRIBUTES.register("swim_speed",
      () -> SWIM_SPEED);

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
  public static RegistryEntry<EntityAttribute> GEODE_DROP_CHANCE_ATTRIBUTE;
  public static RegistryEntry<EntityAttribute> INVULNERABILITY_FRAMES_ATTRIBUTE;
  public static RegistryEntry<EntityAttribute> SWIM_SPEED_ATTRIBUTE;

  public static void initialize() {
    ATTRIBUTES.register();

    PULL_SPEED_ATTRIBUTE = PULL_SPEED_SUPPLIER;
    CRIT_DAMAGE_ATTRIBUTE = CRIT_DAMAGE_SUPPLIER;
    MAX_DURABILITY_ATTRIBUTE = MAX_DURABILITY_SUPPLIER;
    EVASION_ATTRIBUTE = EVASION_SUPPLIER;
    PROJECTILE_SPEED_ATTRIBUTE = PROJECTILE_SPEED_SUPPLIER;
    ARROW_DAMAGE_ATTRIBUTE = ARROW_DAMAGE_SUPPLIER;
    PROJECTILE_COUNT_ATTRIBUTE = PROJECTILE_COUNT_SUPPLIER;
    ARMOR_PIERCE_ATTRIBUTE = ARMOR_PIERCE_SUPPLIER;
    JUMP_COUNT_ATTRIBUTE = JUMP_COUNT_SUPPLIER;
    GEODE_DROP_CHANCE_ATTRIBUTE = GEODE_DROP_CHANCE_SUPPLIER;
    INVULNERABILITY_FRAMES_ATTRIBUTE = INVULNERABILITY_FRAMES_SUPPLIER;
    SWIM_SPEED_ATTRIBUTE = SWIM_SPEED_SUPPLIER;
  }
}
