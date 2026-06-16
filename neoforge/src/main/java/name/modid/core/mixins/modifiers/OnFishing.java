package name.modid.core.mixins.modifiers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.content.events.CustomEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(FishingBobberEntity.class)
public abstract class OnFishing {
  @Shadow
  private int hookCountdown;
  @Shadow
  private Entity hookedEntity;

  @Inject(method = "use", at = @At("HEAD"), cancellable = true)
  private void onFishing(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
    FishingBobberEntity bobber = (FishingBobberEntity) (Object) this;

    if (!(bobber.getOwner() instanceof ServerPlayerEntity player)
        || hookCountdown <= 0) {
      return;
    }

    CustomEvents.ON_FISHING.invoker().onFishing(player, bobber);
  }
}
