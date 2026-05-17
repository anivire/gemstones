package name.modid.core.mixins.other;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.items.registries.GemstonesRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

@Mixin(EnderDragonEntity.class)
public abstract class DropEnderScaleAfterDragonDeath {
  @Shadow
  private int ticksSinceDeath;

  @Unique
  private boolean gemstones$enderScaleDropped;

  @Inject(method = "updatePostDeath", at = @At("TAIL"))
  private void gemstones$dropEnderScaleAfterDeathAnimation(CallbackInfo ci) {
    EnderDragonEntity self = (EnderDragonEntity) (Object) this;

    if (gemstones$enderScaleDropped
        || self.getWorld().isClient()
        || ticksSinceDeath < 200) {
      return;
    }

    gemstones$enderScaleDropped = true;

    ServerWorld world = (ServerWorld) self.getWorld();
    ItemStack drop = new ItemStack(
        GemstonesRegistry.getEnderScaleGemstones().get(0),
        world.random.nextBetween(1, 4));

    Block.dropStack(world, gemstones$getDragonPortalDropPos(world), drop);
  }

  @Unique
  private static BlockPos gemstones$getDragonPortalDropPos(ServerWorld world) {
    int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, 0, 0);

    return new BlockPos(0, y + 1, 0);
  }
}
