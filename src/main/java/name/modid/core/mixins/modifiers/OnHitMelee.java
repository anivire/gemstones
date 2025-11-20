package name.modid.core.mixins.modifiers;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.config.GemstoneModifier;
import name.modid.core.api.modifiers.config.ModifierConfig.HitMeleeConfig;
import name.modid.core.api.modifiers.config.ModifierContext;
import name.modid.core.api.modifiers.config.ModifierManager;
import name.modid.core.api.modifiers.config.ModifierUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(PlayerEntity.class)
public abstract class OnHitMelee {
  private Entity currentTarget;

  @Inject(method = "attack", at = @At("HEAD"))
  private void captureTarget(Entity target, CallbackInfo ci) {
    this.currentTarget = target;
  }

  @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
  private float applyDamageModifiers(float vanillaDamage) {
    PlayerEntity player = (PlayerEntity) (Object) this;

    if (!(currentTarget instanceof LivingEntity livingTarget)) {
      return vanillaDamage;
    }
    if (!(player.getWorld() instanceof ServerWorld serverWorld)) {
      return vanillaDamage;
    }

    ItemStack weapon = player.getMainHandStack();
    // List<GemstoneModifier> modifiersHand =
    // ModifierGatheringHelper.getModifiers(weapon, HitMeleeConfig.class);
    List<GemstoneModifier> modifiers = ModifierUtils.collectGemstoneModifiersFromAllEquipment(
        (ServerPlayerEntity) (Object) this, HitMeleeConfig.class);

    if (modifiers.isEmpty()) {
      return vanillaDamage;
    }

    ModifierContext ctx = new ModifierContext.ContextBuilder(serverWorld)
        .withOwner(player)
        .withTarget(livingTarget)
        .withBaseDamageTaken(vanillaDamage)
        .build();

    ModifierManager.applyModifiers(new ArrayList<>(modifiers), ctx);

    return ctx.getDamageResult();
  }

  @Inject(method = "attack", at = @At("TAIL"))
  private void clearTarget(Entity target, CallbackInfo ci) {
    this.currentTarget = null;
  }
}