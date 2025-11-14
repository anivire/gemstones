package name.modid.core.mixins.trackers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.registries.EffectsRegistry;
import name.modid.core.utils.accessors.SoulBurnEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

@Mixin(LivingEntity.class)
public class SoulBurnTracker implements SoulBurnEntityAccessor {
  private static final TrackedData<Boolean> HAS_SOUL_BURN = DataTracker.registerData(LivingEntity.class,
      TrackedDataHandlerRegistry.BOOLEAN);

  @Inject(method = "initDataTracker", at = @At("TAIL"))
  private void initSoulBurnData(DataTracker.Builder builder, CallbackInfo ci) {
    builder.add(HAS_SOUL_BURN, false);
  }

  @Inject(method = "tick", at = @At("TAIL"))
  private void updateSoulBurnState(CallbackInfo ci) {
    LivingEntity self = (LivingEntity) (Object) this;
    boolean hasSoulBurn = self.hasStatusEffect(EffectsRegistry.SOUL_BURN_EFFECT);

    if (!self.getWorld().isClient) {
      self.getDataTracker().set(HAS_SOUL_BURN, hasSoulBurn);
    }
  }

  public boolean hasSoulBurnEffect() {
    LivingEntity self = (LivingEntity) (Object) this;
    return self.getDataTracker().get(HAS_SOUL_BURN);
  }
}
