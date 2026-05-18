package name.modid.core.mixins.other;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.items.registries.GemstonesRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

@Mixin(EnderDragonEntity.class)
public abstract class DropEnderScaleAfterDragonDeath {
  @Unique
  private boolean gemstones$enderScaleDropped;

  @Inject(method = "updatePostDeath", at = @At(value = "INVOKE",
      target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V",
      ordinal = 0,
      shift = At.Shift.BEFORE))
  private void gemstones$dropEnderScaleWithFirstExperienceOrb(CallbackInfo ci) {
    EnderDragonEntity self = (EnderDragonEntity) (Object) this;

    if (gemstones$enderScaleDropped
        || !(self.getWorld() instanceof ServerWorld world)) {
      return;
    }

    gemstones$enderScaleDropped = true;

    ItemStack drop = new ItemStack(
        GemstonesRegistry.getEnderScaleGemstones().get(0),
        world.random.nextBetween(1, 4));

    Block.dropStack(world, self.getBlockPos(), drop);
  }
}
