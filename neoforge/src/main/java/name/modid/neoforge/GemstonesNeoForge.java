package name.modid.neoforge;

import java.util.Objects;

import name.modid.Gemstones;
import name.modid.GemstonesClient;
import name.modid.core.api.entities.jeweleryTable.JewelryTableScreen;
import name.modid.core.api.particles.BleedParticleFactory;
import name.modid.core.api.particles.ScarabParticleFactory;
import name.modid.core.api.particles.SparkParticleFactory;
import name.modid.core.api.particles.StunnedParticleFactory;
import name.modid.core.content.entities.RainArrowEntity;
import name.modid.core.content.particles.ScarabParticleInstance;
import name.modid.core.content.registries.EntitiesRegistry;
import name.modid.core.content.registries.ParticlesRegistry;
import name.modid.core.content.screen.JewelryTableScreenHandler;
import name.modid.core.content.screen.ScreenRegistry;
import name.modid.neoforge.client.GemstonesNeoForgeClientEvents;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Gemstones.MOD_ID)
public class GemstonesNeoForge {
  public GemstonesNeoForge(IEventBus eventBus) {
    Gemstones.init();

    if (FMLEnvironment.dist.isClient()) {
      eventBus.addListener(this::registerParticleProviders);
      eventBus.addListener(this::clientSetup);
      eventBus.addListener(this::registerScreens);
      eventBus.addListener(this::registerEntityRenderers);

      NeoForge.EVENT_BUS.addListener(GemstonesNeoForgeClientEvents::onItemTooltip);
      NeoForge.EVENT_BUS.addListener(GemstonesNeoForgeClientEvents::onGatherTooltipComponents);
    }
  }

  private void clientSetup(FMLClientSetupEvent event) {
    GemstonesClient.initClient();
  }

  private void registerScreens(RegisterMenuScreensEvent event) {
    ScreenHandlerType<JewelryTableScreenHandler> jewelryTableScreenHandler = Objects
        .requireNonNull(ScreenRegistry.JEWELRY_TABLE_SCREEN_HANDLER.get());

    event.register(
        jewelryTableScreenHandler,
        JewelryTableScreen::new);
  }

  private void registerParticleProviders(RegisterParticleProvidersEvent event) {
    SimpleParticleType bleedParticle = Objects.requireNonNull(ParticlesRegistry.BLEED_PARTICLE.get());
    SimpleParticleType stunnedParticle = Objects.requireNonNull(ParticlesRegistry.STUNNED_PARTICLE.get());
    SimpleParticleType sparkParticle = Objects.requireNonNull(ParticlesRegistry.SPARK_PARTICLE.get());
    ParticleType<ScarabParticleInstance> scarabParticle = Objects
        .requireNonNull(ParticlesRegistry.SCARAB_PARTICLE.get());

    event.registerSpriteSet(bleedParticle, BleedParticleFactory::new);
    event.registerSpriteSet(stunnedParticle, StunnedParticleFactory::new);
    event.registerSpriteSet(sparkParticle, SparkParticleFactory::new);
    event.registerSpriteSet(scarabParticle, ScarabParticleFactory::new);
  }

  private void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    EntityType<RainArrowEntity> rainArrowType = Objects.requireNonNull(EntitiesRegistry.RAIN_ARROW.get());

    event.registerEntityRenderer(
        rainArrowType,
        RainArrowRenderer::new);
  }

  private static class RainArrowRenderer extends ProjectileEntityRenderer<RainArrowEntity> {
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/entity/projectiles/arrow.png");

    public RainArrowRenderer(EntityRendererFactory.Context context) {
      super(context);
    }

    @Override
    public Identifier getTexture(RainArrowEntity entity) {
      return TEXTURE;
    }
  }
}