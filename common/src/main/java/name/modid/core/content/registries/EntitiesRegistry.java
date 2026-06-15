package name.modid.core.content.registries;

import name.modid.Gemstones;
import name.modid.core.content.entities.RainArrowEntity;
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
          .build(Identifier.of(Gemstones.MOD_ID, "spark").toString()));

  public static final EntityType<RainArrowEntity> RAIN_ARROW = Registry.register(
      Registries.ENTITY_TYPE,
      Identifier.of(Gemstones.MOD_ID, "rain_arrow"),
      EntityType.Builder.<RainArrowEntity>create(RainArrowEntity::new, SpawnGroup.MISC)
          .dimensions(0.5f, 0.5f)
          .maxTrackingRange(64)
          .trackingTickInterval(20)
          .build(Identifier.of(Gemstones.MOD_ID, "rain_arrow").toString()));

  public static void init() {
    Gemstones.LOGGER.info("Registering mod entities for {}", Gemstones.MOD_ID);
  }
}
