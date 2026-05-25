package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;

@Mixin(PersistentProjectileEntity.class)
public abstract class ArrowDamageAttribute {

  @ModifyArg(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage" +
      "(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
  private float gemstones$applyBonus(float originalDamage) {
    PersistentProjectileEntity self = (PersistentProjectileEntity) (Object) this;

    return originalDamage + getBonusFrom(self.getWeaponStack());
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
