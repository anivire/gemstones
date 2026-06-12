package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.utils.airJump.AirJumpLogic;
import name.modid.core.utils.airJump.AirJumpState;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public abstract class JumpCountAttribute implements AirJumpState.AirJumpStateHolder {
  @Unique
  private final AirJumpState airJumpState = new AirJumpState();

  @Unique
  private boolean wasOnGround = true;

  @Override
  public AirJumpState getAirJumpState() {
    return airJumpState;
  }

  @Inject(method = "tick", at = @At("TAIL"))
  private void airJumpTick(CallbackInfo ci) {
    PlayerEntity self = (PlayerEntity) (Object) this;
    AirJumpLogic.tick(self);

    if (self.isOnGround()) {
      this.trackGroundTransition();
    } else {
      wasOnGround = false;
    }
  }

  @Unique
  private void trackGroundTransition() {
    PlayerEntity self = (PlayerEntity) (Object) this;
    if (!wasOnGround && self.isOnGround()) {
      AirJumpLogic.onLanded(self);
    }
    wasOnGround = true;
  }
}