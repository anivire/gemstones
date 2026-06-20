package name.modid.core.mixins.trackers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.registries.EffectsRegistry;
import name.modid.core.utils.accessors.StunnedEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

@Mixin(LivingEntity.class)
public class StunnedTracker implements StunnedEntityAccessor {
  private static final TrackedData<Boolean> HAS_STUNNED = DataTracker.registerData(LivingEntity.class,
      TrackedDataHandlerRegistry.BOOLEAN);

  @Inject(method = "initDataTracker", at = @At("TAIL"))
  private void initStunnedData(DataTracker.Builder builder, CallbackInfo ci) {
    builder.add(HAS_STUNNED, false);
  }

  @Inject(method = "tick", at = @At("TAIL"))
  private void updateStunnedState(CallbackInfo ci) {
    LivingEntity self = (LivingEntity) (Object) this;

    if (!self.getWorld().isClient) {
      self.getDataTracker().set(HAS_STUNNED, hasStunnedStatusEffect(self));
    }
  }

  @Override
  public boolean hasStunnedEffect() {
    LivingEntity self = (LivingEntity) (Object) this;
    return hasStunnedStatusEffect(self)
        || self.getDataTracker().get(HAS_STUNNED);
  }

  private boolean hasStunnedStatusEffect(LivingEntity self) {
    return self.hasStatusEffect(EffectsRegistry.stunnedEntry());
  }
}
