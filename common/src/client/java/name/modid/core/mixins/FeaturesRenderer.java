package name.modid.core.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.features.StunnedStarsFeature;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
public abstract class FeaturesRenderer<T extends LivingEntity, M extends EntityModel<T>> {
  @Invoker("addFeature")
  public abstract boolean invokeAddFeature(FeatureRenderer<?, ?> feature);

  @SuppressWarnings({ "unchecked" })
  @Inject(at = @At("RETURN"), method = "<init>")
  public void init(EntityRendererFactory.Context ctx, EntityModel<?> model, float shadowRadius,
      CallbackInfo info) {
    FeatureRendererContext<T, M> context = (FeatureRendererContext<T, M>) (Object) this;

    this.invokeAddFeature(new StunnedStarsFeature<>(context));
  }
}
