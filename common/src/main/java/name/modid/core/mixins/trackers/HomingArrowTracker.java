package name.modid.core.mixins.trackers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.utils.HomingArrow;
import name.modid.core.utils.accessors.HomingArrowAccessor;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.world.World;

@Mixin(PersistentProjectileEntity.class)
public class HomingArrowTracker implements HomingArrowAccessor {
  @Unique
  private final DataTracker dataTrackerRef = ((PersistentProjectileEntity) (Object) this).getDataTracker();

  @Unique
  private static final TrackedData<Boolean> IS_HOMING = DataTracker.registerData(PersistentProjectileEntity.class,
      TrackedDataHandlerRegistry.BOOLEAN);

  @Inject(method = "initDataTracker", at = @At("TAIL"))
  private void initData(DataTracker.Builder builder, CallbackInfo ci) {
    builder.add(IS_HOMING, false);
  }

  @Inject(method = "tick", at = @At("TAIL"))
  private void tickHoming(CallbackInfo ci) {
    PersistentProjectileEntity arrow = (PersistentProjectileEntity) (Object) this;
    World world = arrow.getWorld();

    if (!world.isClient && arrow.getDataTracker().get(IS_HOMING)) {
      HomingArrow.tickHomingArrow(arrow);
    }
  }

  @Override
  public void setHoming(boolean value) {
    ((PersistentProjectileEntity) (Object) this).getDataTracker().set(IS_HOMING, value);
  }

  @Override
  public boolean isHoming() {
    return ((PersistentProjectileEntity) (Object) this).getDataTracker().get(IS_HOMING);
  }
}