package name.modid.core.mixins.other;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.api.modifiers.helpers.ModifierGatheringHelper;
import name.modid.core.api.modifiers.types.EventType;
import name.modid.core.utils.HomingArrow;
import name.modid.core.utils.accessors.HomingArrowAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(CrossbowItem.class)
public class MarkHomingArrowCrossbow {

  @Inject(method = "shootAll", at = @At("TAIL"))
  private static void onShootAll(
      World world,
      LivingEntity entity,
      Hand hand,
      ItemStack stack,
      float speed,
      float divergence,
      LivingEntity target,
      CallbackInfo ci) {
    if (world.isClient() || !(entity instanceof PlayerEntity player)) {
      return;
    }

    if (!ModifierGatheringHelper.getModifiersByEventType(stack, EventType.HOMING_ARROW).isEmpty()) {
      LivingEntity preferredTarget = HomingArrow.findPreferredTarget((ServerWorld) world, player);

      world.getEntitiesByClass(PersistentProjectileEntity.class,
          player.getBoundingBox().expand(5.0),
          e -> e.getOwner() == player && e.age < 5)
          .forEach(arrow -> {
            ((HomingArrowAccessor) arrow).setHoming(true);
            HomingArrow.setPreferredTarget(arrow, preferredTarget);
          });
    }
  }
}
