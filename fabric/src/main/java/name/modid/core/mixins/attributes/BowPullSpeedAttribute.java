package name.modid.core.mixins.attributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import name.modid.core.utils.BowPullHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(BowItem.class)
public class BowPullSpeedAttribute {
  // The pull progress is computed from the bow that is actually released. A shared static
  // multiplier leaked the pull speed of one player to every other bow user on the server.
  @ModifyVariable(method = "onStoppedUsing", at = @At("STORE"), ordinal = 0)
  private float gemstones$applyPullSpeed(
      float pullProgress,
      ItemStack stack,
      World world,
      LivingEntity user,
      int remainingUseTicks) {
    return BowPullHelper.getPullProgress(stack, stack.getMaxUseTime(user) - remainingUseTicks);
  }
}
