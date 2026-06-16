package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public abstract class SwimSpeedAttribute {
  @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V", ordinal = 0), index = 0)
  private float applySwimSpeedAttribute(float speed) {
    LivingEntity self = (LivingEntity) (Object) this;
    double swimSpeedDelta = ModifierUtils.collectAttributeValuesFromArmor(self, AttributesRegistry.SWIM_SPEED_ATTRIBUTE);

    return (float) (speed * Math.max(0.01, 1.0 + swimSpeedDelta));
  }
}
