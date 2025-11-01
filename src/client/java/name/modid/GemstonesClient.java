package name.modid;

import name.modid.core.api.entities.JewelryTableBlockEntityRenderer;
import name.modid.core.api.entities.JewelryTableScreen;
import name.modid.core.api.models.ModelsRegistry;
import name.modid.core.api.particles.ClientParticlesRegistry;
import name.modid.core.api.projectiles.spark.SparkProjectileModel;
import name.modid.core.api.projectiles.spark.SparkProjectileRenderer;
import name.modid.core.content.blocks.entity.core.BlockEntitiesRegistry;
import name.modid.core.content.registries.EntitiesRegistry;
import name.modid.core.content.screen.ScreenRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class GemstonesClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    EntityModelLayerRegistry.registerModelLayer(SparkProjectileModel.SPARK, SparkProjectileModel::getTexturedModelData);
    EntityRendererRegistry.register(EntitiesRegistry.SPARK_ENTITY, SparkProjectileRenderer::new);

    BlockEntityRendererFactories.register(BlockEntitiesRegistry.JEWELRY_TABLE_BLOCK_ENTITY,
        JewelryTableBlockEntityRenderer::new);
    HandledScreens.register(ScreenRegistry.JEWELRY_TABLE_SCREEN_HANDLER, JewelryTableScreen::new);

    ModelsRegistry.initialize();
    ClientParticlesRegistry.initialize();
  }
}
