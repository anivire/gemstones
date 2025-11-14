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

  @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"), cancellable = true, require = 0)
  private void gemstones$onDamage(
      DamageSource source,
      float amount,
      CallbackInfoReturnable<Boolean> cir) {
    LivingEntity self = (LivingEntity) (Object) this;

    double fromArmor = ModifierUtils.collectAttributeValuesFromArmor(
        self, AttributesRegistry.EVASION_ATTRIBUTE);
    double fromItem = ModifierUtils.collectAttributeValuesFromItem(
        self, AttributesRegistry.EVASION_ATTRIBUTE);

    double evasionChance = Math.max(0.0, Math.min(1.0, fromArmor + fromItem));
    boolean evaded = self.getRandom().nextDouble() < evasionChance;

    if (evaded) {
      cir.setReturnValue(false);

      if (self.getWorld() instanceof ServerWorld serverWorld) {
        serverWorld.playSound(
            null,
            self.getX(), self.getY(), self.getZ(),
            SoundEvents.ENTITY_ENDERMAN_TELEPORT,
            SoundCategory.PLAYERS,
            1.0F,
            1.0F);
        serverWorld.spawnParticles(
            ParticleTypes.CLOUD,
            self.getX(), self.getY() + 1.0, self.getZ(),
            20,
            0.3, 0.3, 0.3,
            0.2);
      }
    }
  }
}