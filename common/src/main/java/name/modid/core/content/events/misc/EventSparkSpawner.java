package name.modid.core.content.events.misc;

import name.modid.core.content.entities.SparkProjectileEntity;
import name.modid.core.content.registries.EffectsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class EventSparkSpawner {
  public static void setup(LivingEntity entity, DamageSource damageSource) {
    if (!(entity.getWorld() instanceof ServerWorld serverWorld)
        || !entity.hasStatusEffect(EffectsRegistry.radianceEntry())) {
      return;
    }

    int SPARK_COUNT = 3;

    for (int i = 0; i < SPARK_COUNT + entity.getStatusEffect(EffectsRegistry.radianceEntry()).getAmplifier(); i++) {
      SparkProjectileEntity shard = new SparkProjectileEntity(serverWorld, entity);

      shard.setPosition(entity.getX(),
          entity.getY() + entity.getHeight() / 2.0, entity.getZ());

      double yaw = 2 * Math.PI * serverWorld.random.nextDouble();
      double pitch = Math.PI * serverWorld.random.nextDouble() * 0.5;
      double x = Math.cos(yaw) * Math.sin(pitch);
      double y = Math.cos(pitch);
      double z = Math.sin(yaw) * Math.sin(pitch);
      Vec3d dir = new Vec3d(x, y, z).normalize();

      shard.setVelocity(dir.multiply(0.22 + serverWorld.random.nextDouble() * 0.10));
      serverWorld.spawnEntity(shard);
    }
  }
}
