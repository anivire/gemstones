package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.modid.core.api.modifiers.config.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

@Mixin(LivingEntity.class)
public class EvasionAttribute {
  @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"), cancellable = true)
  private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    LivingEntity entity = (LivingEntity) (Object) this;

    double evasionChance = Math.min(0.0,
        ModifierUtils.collectAttributeValuesFromArmor(entity, AttributesRegistry.EVASION_ATTRIBUTE)
            + ModifierUtils.collectAttributeValuesFromItem(entity, AttributesRegistry.EVASION_ATTRIBUTE));

    if (entity.getRandom().nextDouble() < evasionChance
        && entity.getWorld() instanceof ServerWorld serverWorld) {
      cir.setReturnValue(false);

      serverWorld.playSound(
          null,
          entity.getX(),
          entity.getY(),
          entity.getZ(),
          SoundEvents.ENTITY_ENDERMAN_TELEPORT,
          SoundCategory.PLAYERS,
          1.0F,
          1.0F);

      serverWorld.spawnParticles(
          ParticleTypes.CLOUD,
          entity.getX(),
          entity.getY() + 1.0,
          entity.getZ(),
          20,
          0.3,
          0.3,
          0.3,
          0.2);
    }
  }
}
