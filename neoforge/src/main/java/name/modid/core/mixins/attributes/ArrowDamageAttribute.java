package name.modid.core.mixins.attributes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;

@Mixin(PersistentProjectileEntity.class)
public abstract class ArrowDamageAttribute {

  @WrapOperation(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage" +
      "(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
  private boolean gemstones$applyBonus(Entity target, DamageSource source, float originalDamage,
      Operation<Boolean> original) {
    PersistentProjectileEntity self = (PersistentProjectileEntity) (Object) this;

    return original.call(target, source, originalDamage + getBonusFrom(self.getWeaponStack()));
  }

  private float getBonusFrom(ItemStack shotFrom) {
    if (shotFrom == null || shotFrom.isEmpty())
      return 0;

    AttributeModifiersComponent modifiers = shotFrom.getOrDefault(
        DataComponentTypes.ATTRIBUTE_MODIFIERS,
        AttributeModifiersComponent.DEFAULT);

    return (float) ModifierUtils.getAttributeDelta(
        modifiers,
        AttributesRegistry.ARROW_DAMAGE_ATTRIBUTE,
        1.0);
  }
}
