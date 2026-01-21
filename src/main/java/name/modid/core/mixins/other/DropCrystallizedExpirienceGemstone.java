package name.modid.core.mixins.other;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.items.registries.GemstonesRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@Mixin(ExperienceBottleEntity.class)
public abstract class DropCrystallizedExpirienceGemstone {
  private static final float DROP_CHANCE = 0.02f;

  @Inject(method = "onCollision", at = @At("TAIL"))
  private void gemstones$dropCrystallizedExperience(
      net.minecraft.util.hit.HitResult hitResult,
      CallbackInfo ci) {
    ExperienceBottleEntity self = (ExperienceBottleEntity) (Object) this;

    if (self.getWorld().isClient()
        || self.getWorld().random.nextFloat() > DROP_CHANCE) {
      return;
    }

    ServerWorld world = (ServerWorld) self.getWorld();
    BlockPos pos = self.getBlockPos();

    ItemStack drop = new ItemStack(
        GemstonesRegistry
            .getCrystallizedExpirienceGemstones()
            .get(0));

    Block.dropStack(world, pos, drop);
  }
}