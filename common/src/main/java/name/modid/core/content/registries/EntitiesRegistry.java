package name.modid.core.content.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import name.modid.Gemstones;
import name.modid.core.content.entities.RainArrowEntity;
import name.modid.core.content.entities.SparkProjectileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKeys;

public class EntitiesRegistry {
  public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Gemstones.MOD_ID,
      RegistryKeys.ENTITY_TYPE);

  public static final RegistrySupplier<EntityType<SparkProjectileEntity>> SPARK_ENTITY = ENTITY_TYPES.register("spark",
      () -> EntityType.Builder.<SparkProjectileEntity>create(
          SparkProjectileEntity::new,
          SpawnGroup.MISC)
          .dimensions(0.25f, 0.25f)
          .maxTrackingRange(64)
          .trackingTickInterval(10)
          .build(Gemstones.MOD_ID + ":spark"));

  public static final RegistrySupplier<EntityType<RainArrowEntity>> RAIN_ARROW = ENTITY_TYPES.register("rain_arrow",
      () -> EntityType.Builder.<RainArrowEntity>create(
          RainArrowEntity::new,
          SpawnGroup.MISC)
          .dimensions(0.5f, 0.5f)
          .maxTrackingRange(64)
          .trackingTickInterval(20)
          .build(Gemstones.MOD_ID + ":rain_arrow"));

  public static void initialize() {
    ENTITY_TYPES.register();
    Gemstones.LOGGER.info("Registering mod entities for {}", Gemstones.MOD_ID);
  }
}