package name.modid;

import name.modid.core.api.models.ModelsRegistry;
import name.modid.core.api.particles.ClientParticlesRegistry;
import name.modid.core.api.projectiles.spark.SparkProjectileModel;
import name.modid.core.api.projectiles.spark.SparkProjectileRenderer;
import name.modid.core.content.registries.EntitiesRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class GemstonesClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    EntityModelLayerRegistry.registerModelLayer(SparkProjectileModel.SPARK, SparkProjectileModel::getTexturedModelData);
    EntityRendererRegistry.register(EntitiesRegistry.SPARK_ENTITY, SparkProjectileRenderer::new);

    ModelsRegistry.initialize();
    ClientParticlesRegistry.initialize();
  }
}
