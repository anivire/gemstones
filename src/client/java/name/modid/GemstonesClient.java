package name.modid;

import name.modid.core.api.ClientKeyHandler;
import name.modid.core.api.OreHighlighter;
import name.modid.core.api.entities.jeweleryTable.JewelryTableBlockEntityRenderer;
import name.modid.core.api.entities.jeweleryTable.JewelryTableScreen;
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
import net.minecraft.client.render.entity.ArrowEntityRenderer;

public class GemstonesClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    EntityModelLayerRegistry.registerModelLayer(SparkProjectileModel.SPARK, SparkProjectileModel::getTexturedModelData);

    OreHighlighter.register();
    HandledScreens.register(ScreenRegistry.JEWELRY_TABLE_SCREEN_HANDLER, JewelryTableScreen::new);
    EntityRendererRegistry.register(EntitiesRegistry.SPARK_ENTITY, SparkProjectileRenderer::new);
    EntityRendererRegistry.register(EntitiesRegistry.RAIN_ARROW, context -> new ArrowEntityRenderer(context));
    BlockEntityRendererFactories.register(
        BlockEntitiesRegistry.JEWELRY_TABLE_BLOCK_ENTITY,
        JewelryTableBlockEntityRenderer::new);

    ModelsRegistry.initialize();
    ClientParticlesRegistry.initialize();
    ClientKeyHandler.initialize();

  }
}
