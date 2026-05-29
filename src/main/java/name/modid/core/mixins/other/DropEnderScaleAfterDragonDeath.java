package name.modid.core.mixins.other;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.modid.core.content.items.registries.GemstonesRegistry;
import name.modid.datapack.drops.DropsConfig;
import name.modid.datapack.drops.DropsRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;

@Mixin(EnderDragonEntity.class)
public abstract class DropEnderScaleAfterDragonDeath {
  @Unique
  private boolean enderScaleDropped;

  @Inject(method = "updatePostDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V", ordinal = 0, shift = At.Shift.BEFORE))
  private void dropEnderScaleWithFirstExperienceOrb(CallbackInfo ci) {
    EnderDragonEntity self = (EnderDragonEntity) (Object) this;

    if (enderScaleDropped
        || !(self.getWorld() instanceof ServerWorld world)) {
      return;
    }

    DropsConfig.SpecialDrop config = DropsRegistry.getSpecialDrop("ender_dragon_death");
    if (config == null)
      return;

    enderScaleDropped = true;

    Item dropItem = resolveItem(config);
    if (dropItem == null)
      return;

    int count = config.getMinCount() + world.random.nextInt(config.getMaxCount() - config.getMinCount() + 1);
    Block.dropStack(world, self.getBlockPos(), new ItemStack(dropItem, count));
  }

  private static Item resolveItem(DropsConfig.SpecialDrop config) {
    if (config.getGemstoneType() != null) {
      List<Item> gemstones = GemstonesRegistry.getGemstonesByType(config.getGemstoneType());
      if (!gemstones.isEmpty()) {
        return gemstones.get(0);
      }
    }
    if (config.getItem() != null) {
      Item item = Registries.ITEM.get(config.getItem());
      if (item != net.minecraft.item.Items.AIR) {
        return item;
      }
    }
    return null;
  }
}
