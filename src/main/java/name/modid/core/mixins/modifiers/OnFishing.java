package name.modid.core.mixins.modifiers;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.OnFishingConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierContext.ContextBuilder;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(FishingBobberEntity.class)
public abstract class OnFishing {
  @Shadow
  private int hookCountdown;
  @Shadow
  private Entity hookedEntity;

  @Inject(method = "use", at = @At("HEAD"), cancellable = true)
  private void onFishing(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
    FishingBobberEntity self = (FishingBobberEntity) (Object) this;

    if (!(self.getOwner() instanceof ServerPlayerEntity player)) {
      return;
    }

    if (player.getWorld() instanceof ServerWorld serverWorld
        && hookCountdown > 0) {
      List<GemstoneModifier> modifiers = ModifierUtils.collectValuesFromArmor(
          player,
          armorPiece -> ModifierGatheringHelper
              .getModifiers(armorPiece, OnFishingConfig.class)
              .stream()
              .toList());

      ContextBuilder ctxBuilder = new ContextBuilder(serverWorld)
          .withOwner(player)
          .withTarget(self);
      ModifierContext ctx = ctxBuilder.build();
      ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);
    }
  }
}