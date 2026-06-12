package name.modid;

import dev.architectury.platform.Platform;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import name.modid.core.api.ClientDatapackSyncHandler;
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
import net.minecraft.client.render.entity.ArrowEntityRenderer;

public class GemstonesClient {
  public static void initClient() {
    EntityModelLayerRegistry.register(
        SparkProjectileModel.SPARK,
        SparkProjectileModel::getTexturedModelData);

    OreHighlighter.registerNetworking();

    if (!Platform.isNeoForge()) {
      MenuRegistry.registerScreenFactory(
          ScreenRegistry.JEWELRY_TABLE_SCREEN_HANDLER.get(),
          JewelryTableScreen::new);
    }

    EntityRendererRegistry.register(
        () -> EntitiesRegistry.SPARK_ENTITY,
        SparkProjectileRenderer::new);

    EntityRendererRegistry.register(
        () -> EntitiesRegistry.RAIN_ARROW,
        context -> new ArrowEntityRenderer(context));

    BlockEntityRendererRegistry.register(
        BlockEntitiesRegistry.JEWELRY_TABLE_BLOCK_ENTITY.get(),
        JewelryTableBlockEntityRenderer::new);

    ModelsRegistry.initialize();
    ClientDatapackSyncHandler.initialize();
    ClientParticlesRegistry.initialize();
    ClientKeyHandler.initialize();
  }
}