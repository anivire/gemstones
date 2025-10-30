package name.modid.core.content.registries;

import name.modid.Gemstones;
import name.modid.core.content.entities.SparkProjectileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EntitiesRegistry {
  public static final EntityType<SparkProjectileEntity> SPARK_ENTITY = Registry.register(
      Registries.ENTITY_TYPE,
      Identifier.of(Gemstones.MOD_ID, "spark"),
      EntityType.Builder.<SparkProjectileEntity>create(SparkProjectileEntity::new, SpawnGroup.MISC)
          .dimensions(0.25f, 0.25f)
          .maxTrackingRange(64)
          .trackingTickInterval(10)
          .build());

  // // ИСПРАВЛЕНО: Используем ванильный EntityType.Builder
  // public static final EntityType<HomingSkullProjectileEntity>
  // HOMING_SKULL_PROJECTILE = Registry.register(
  // Registries.ENTITY_TYPE,
  // Identifier.of(Gemstones.MOD_ID, "homing_skull_projectile"), // <--
  // ИСПРАВЛЕНО: Правильная ссылка на MOD_ID
  // EntityType.Builder.<HomingSkullProjectileEntity>create(HomingSkullProjectileEntity::new,
  // SpawnGroup.MISC)
  // .dimensions(0.5f, 0.5f)
  // .maxTrackingRange(4) // <-- Современный способ
  // .trackingTickInterval(10) // <-- Современный способ
  // .build());

  public static void init() {
    Gemstones.LOGGER.info("Registering mod entities for {}", Gemstones.MOD_ID);
  }
}
