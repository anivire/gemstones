package name.modid.core.mixins.attributes;

import java.util.ArrayList;
import java.util.List;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import name.modid.core.api.modifiers.config.utils.ModifierUtils;
import name.modid.core.content.registries.AttributesRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

// Extra projectiles go through the vanilla multishot path: they are added to the loaded projectiles
// and vanilla shoots them, so the bow and crossbow code stays intact for other mods
@Mixin(RangedWeaponItem.class)
public abstract class ProjectileCountAttribute {
  @Unique
  private static final float BOW_SPREAD = 10.0F;
  @Unique
  private static final float CROSSBOW_SPREAD = 7.0F;

  // Extra arrows are copies that cannot be picked up, like the vanilla multishot arrows
  @ModifyReturnValue(method = "load", at = @At("RETURN"))
  private static List<ItemStack> gemstones$addExtraProjectiles(List<ItemStack> projectiles,
      @Local(argsOnly = true, ordinal = 0) ItemStack weaponStack,
      @Local(argsOnly = true) LivingEntity shooter) {
    int extra = gemstones$getExtraProjectiles(weaponStack, shooter);
    if (extra <= 0 || projectiles.isEmpty() || !(shooter.getWorld() instanceof ServerWorld)) {
      return projectiles;
    }

    ItemStack template = projectiles.get(0).copyWithCount(1);
    template.set(DataComponentTypes.INTANGIBLE_PROJECTILE, Unit.INSTANCE);

    List<ItemStack> result = new ArrayList<>(projectiles);
    for (int i = 0; i < extra; i++) {
      result.add(template.copy());
    }
    return result;
  }

  // Without Multishot vanilla has no spread, so the extra projectiles would fly as one
  @ModifyExpressionValue(method = "shootAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getProjectileSpread(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/Entity;F)F"))
  private float gemstones$applyExtraProjectileSpread(float spread,
      @Local(argsOnly = true) ItemStack weaponStack,
      @Local(argsOnly = true, ordinal = 0) LivingEntity shooter,
      @Local(argsOnly = true) List<ItemStack> projectiles) {
    if (spread > 0.0F || projectiles.size() <= 1 || gemstones$getExtraProjectiles(weaponStack, shooter) <= 0) {
      return spread;
    }

    return weaponStack.getItem() instanceof CrossbowItem ? CROSSBOW_SPREAD : BOW_SPREAD;
  }

  @Unique
  private static int gemstones$getExtraProjectiles(ItemStack weaponStack, LivingEntity shooter) {
    if (!(shooter instanceof PlayerEntity)) {
      return 0;
    }

    AttributeModifiersComponent mods = weaponStack
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
    int projectileCount = (int) Math.floor(ModifierUtils.getAttributeValue(
        mods,
        AttributesRegistry.PROJECTILE_COUNT_ATTRIBUTE,
        1.0));
    return Math.max(1, projectileCount) - 1;
  }
}
