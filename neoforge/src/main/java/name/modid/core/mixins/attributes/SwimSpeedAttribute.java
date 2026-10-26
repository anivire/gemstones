package name.modid.core.mixins.attributes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

@Mixin(LivingEntity.class)
public abstract class SwimSpeedAttribute {
  @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
  private void applySwimSpeedAttribute(LivingEntity self, float speed, Vec3d movementInput,
      Operation<Void> original) {
    double swimSpeedDelta = ModifierUtils.collectAttributeValuesFromArmor(self, AttributesRegistry.SWIM_SPEED_ATTRIBUTE);

    original.call(self, (float) (speed * Math.max(0.01, 1.0 + swimSpeedDelta)), movementInput);
  }
}
